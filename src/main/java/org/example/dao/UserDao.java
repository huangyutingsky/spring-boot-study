package org.example.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ParentTableInfoEntity;
import org.example.entity.ServerEntity;
import org.example.entity.ShareTableInfoEntity;
import org.example.entity.UserEntity;
import org.example.enumeration.TableDdlEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.jws.Oneway;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/20 18:16
 */
@Slf4j
@Component
public class UserDao {

    private static final String PARENT_TABLE = "user";

    private static final Pattern DATE_FIELD_PATTERN = Pattern.compile(".*Time$");

    @Autowired
    private ParentTableInfoDao parentTableInfoDao;
    @Autowired
    private ShareTableInfoDao shareTableInfoDao;
    @Autowired
    private ServerDao serverDao;
    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询
     *
     * @param sql
     */
    public void query(String sql) {
        final ArrayList<SqlExecuteInfo> sqlExecuteInfos = new ArrayList<>();
        final ArrayList<UserEntity> allDatas = new ArrayList<>();

        // 根据表名获取所有分片表对应的分片表信息与对应服务器信息
        final List<ShareTableInfoEntity> shareTableInfos = shareTableInfoDao.getServerInfoByParentTableName(PARENT_TABLE);

        // 根据数据源信息，从Spring容器获取对应的数据源Bean
        for (ShareTableInfoEntity shareTableInfo : shareTableInfos) {
            // 获取数据源名称
            final String dsName = shareTableInfo.getServerEntity().getDsName();
            // 获取分片表名称
            final String shareTableName = shareTableInfo.getName();
            // 根据数据源名称从Spring容器获取对应的数据源Bean
            final DruidDataSource ds = applicationContext.getBean(dsName, DruidDataSource.class);
            // 封装执行sql信息体
            sqlExecuteInfos.add(SqlExecuteInfo.builder()
                    .dataSource(ds)
                    .tableName(shareTableName)
                    .build());
        }

        if (CollectionUtils.isEmpty(sqlExecuteInfos)) {
            return;
        }

        // 针对select * from table where
        for (SqlExecuteInfo sqlExecuteInfo : sqlExecuteInfos) {
            try (Connection conn = sqlExecuteInfo.getDataSource().getConnection();
                 Statement statement = conn.createStatement()) {
                final String newSql = sql.replace("{table}", sqlExecuteInfo.getTableName());
                final ResultSet resultSet = statement.executeQuery(newSql);
                final List<UserEntity> objects = resultSetToBean(resultSet, UserEntity.class);
                allDatas.addAll(objects);
                // TODO: 2022/4/21 order by、limit、group by处理 
                log.info("tableName={} 条数={}", sqlExecuteInfo.getTableName(), objects.size());
            } catch (SQLException e) {
                log.info("conn异常:{}", e.getMessage(), e);
            } catch (Exception e) {
                log.info("resultSetToBean异常:{}", e.getMessage(), e);
            }
        }

        log.info("查询成功: size={} data={}", allDatas.size(), allDatas);
    }

    /**
     * 新增
     *
     * @param sql sql语句
     */
    public void insert(String sql) {
        // 根据父表名查询最新的分片表信息
        final ShareTableInfoEntity shareTableInfo = shareTableInfoDao.getLatestShareTableInfoByParentTableName(PARENT_TABLE);
        final Integer max = shareTableInfo.getMaxRows();
        final Integer rows = shareTableInfo.getRows();
        // TODO: 2022/4/21 并发情况是否允许?
        // 该表记录数小于五百万
        if (rows < max) {
            // 获取数据源名称
            final String dsName = shareTableInfo.getServerEntity().getDsName();
            // 获取分片表名称
            final String shareTableName = shareTableInfo.getName();
            // 根据数据源名称从Spring容器获取对应的数据源Bean
            final DruidDataSource ds = applicationContext.getBean(dsName, DruidDataSource.class);
            final SqlExecuteInfo sqlExecuteInfo = SqlExecuteInfo.builder()
                    .dataSource(ds)
                    .tableName(shareTableName)
                    .build();

            String insertSql = null;
            try (Connection conn = ds.getConnection();
                 Statement statement = conn.createStatement()) {
                // 获取该表全局唯一uuid
                String uuid = Objects.requireNonNull(redisTemplate.opsForValue().increment("uuid:" + PARENT_TABLE)).toString();
                insertSql = sql.replace("{table}", sqlExecuteInfo.getTableName()).replace("{id}", uuid);
                statement.execute(insertSql);
                log.info("新增数据成功,sql={}", insertSql);
            } catch (SQLException e) {
                log.info("conn异常:{} ,sql={}", e.getMessage(), insertSql, e);
            }
            // 更新对应分片信息
            shareTableInfoDao.incrRowNumById(shareTableInfo.getId());
        } else {
            // 该表记录数大于等于五百万
            log.info("{}表记录数超过五百万", shareTableInfo.getName());
            String lockName = "createTableLock:" + PARENT_TABLE;
            final Boolean getLock = redisTemplate.opsForValue().setIfAbsent(lockName, 1, Duration.ofMinutes(30));
            if (getLock != null && getLock) {
                // 获取到创建表锁
                // 选择分片表所在服务的数据源，默认选择id=1的服务（在哪个服务上建表）
                final ServerEntity defaultServer = serverDao.getDefaultServer();
                // 根据数据源名称从Spring容器获取对应的数据源Bean
                final DruidDataSource ds = applicationContext.getBean(defaultServer.getDsName(), DruidDataSource.class);
                // 创建表
                String createTableSql = null;
                try (Connection conn = ds.getConnection();
                     Statement statement = conn.createStatement()) {
                    // 查询父表所有信息
                    ParentTableInfoEntity parentTableInfo = parentTableInfoDao.selectByName(PARENT_TABLE);
                    int count = parentTableInfo.getShareTableInfos().size();
                    // 新的分片表：父表名_分片数
                    String newShareTableName = PARENT_TABLE + "_" + count;
                    createTableSql = TableDdlEnum.USER.getDdl().replace("{table}", newShareTableName);
                    // 创建表
                    statement.execute(createTableSql);
                    log.info("创建表成功,sql={}", createTableSql);
                    // 插入分片表信息
                    shareTableInfoDao.insert(ShareTableInfoEntity.builder()
                            .createTime(new Date())
                            .updateTime(new Date())
                            .incrNum(count + 1)
                            .name(newShareTableName)
                            .parentTableId(parentTableInfo.getId())
                            .serverId(defaultServer.getId())
                            .build());
                    // 更新父表分片数 + 1
                    parentTableInfoDao.updateIncrNumMax();
                } catch (SQLException e) {
                    log.info("conn异常:{} ,sql={}", e.getMessage(), createTableSql, e);
                } finally {
                    // 释放创建表锁
                    redisTemplate.delete(lockName);
                }
            } else {
                // 未获取到创建表锁，将该sql放入队列
                String queue = "queue:insertUserQueue";
                redisTemplate.opsForList().leftPush(queue, sql);
            }
        }
    }


    public static <T> List<T> resultSetToBean(ResultSet resultSet, Class beanClass) throws Exception {
        // 获取Bean对象内的所有属性
        Field[] fields = beanClass.getDeclaredFields();
        List<T> beanList = new ArrayList<>();
        if (resultSet != null) {
            while (resultSet.next()) {
                // 每当有一行数据就创建一个Bean对象
                T object = (T) beanClass.newInstance();
                for (Field field : fields) {
                    String fieldName = field.getName();
                    if ("serialVersionUID".equals(fieldName)) {
                        continue;
                    }
                    // 利用字符串拼接，将属性名的首字母变为大写，获取对应的set方法。
                    Method setField = beanClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
                    Matcher matcher = DATE_FIELD_PATTERN.matcher(fieldName);
                    // 这里对Date类型的字段赋值进行区分赋值，直接赋值会报错
                    if (!matcher.matches()) {
                        setField.invoke(object, resultSet.getObject(toUnderlineCase(fieldName)));
                    } else {
                        setField.invoke(object, new Date(resultSet.getTimestamp(toUnderlineCase(fieldName)).getTime()));
                    }
                }
                beanList.add(object);
            }
        }
        return beanList;
    }

    /**
     * 驼峰转 下划线
     * userName  ---->  user_name
     * user_name  ---->  user_name
     *
     * @param camelCaseStr 驼峰字符串
     * @return 带下滑线的String
     */
    public static String toUnderlineCase(String camelCaseStr) {
        if (camelCaseStr == null) {
            return null;
        }
        // 将驼峰字符串转换成数组
        char[] charArray = camelCaseStr.toCharArray();
        StringBuffer buffer = new StringBuffer();
        //处理字符串
        for (int i = 0, l = charArray.length; i < l; i++) {
            if (charArray[i] >= 65 && charArray[i] <= 90) {
                buffer.append("_").append(charArray[i] += 32);
            } else {
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }
}

@Data
@Builder
class SqlExecuteInfo {
    private DataSource dataSource;
    private String tableName;
}