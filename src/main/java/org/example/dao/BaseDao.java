package org.example.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;
import org.example.enumeration.RedisKeyEnum;
import org.example.enumeration.SqlEnum;
import org.example.util.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.Date;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/20 18:16
 */
@Slf4j
public abstract class BaseDao<T> {

    /**
     * sql表名占位符
     */
    public static final String TABLE_NAME_PLACEHOLDER = "{table}";
    /**
     * 父表名
     */
    protected String parentTable;
    /**
     * 延迟sql执行队列
     */
    protected String sqlQueue;

    @Autowired
    private ParentTableInfoDao parentTableInfoDao;
    @Autowired
    private ShareTableInfoDao shareTableInfoDao;
    @Autowired
    private ServerDao serverDao;
    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    /**
     * 批量插入
     *
     * @param batchData 数据列表
     */
    public void insertBatch(List<T> batchData) {
        // 存储相同ds数据源、相同表名的新增用户数据（key-数据源、表名 value-用户列表）
        final Map<DataSourceTableMap, List<T>> dsBeansMap = new HashMap<>();

        for (T data : batchData) {
            // 默认分表策略:根据父表名查询最新的分片表信息（每条数据插入表时，根据策略选取不同的数据源）
            final ShareTableInfoEntity shareTableInfo = shareTableInfoDao.getLatestShareTableInfoByParentTableName(this.parentTable);
            // 获取数据源名称
            final String dsName = shareTableInfo.getServerEntity().getDsName();
            // 根据数据源名称从Spring容器获取对应的数据源Bean
            final DataSource ds = applicationContext.getBean(dsName, DataSource.class);
            // 获取分表最大限制行数
            final int max = shareTableInfo.getMaxRows();
            // 获取分表记录数
            String key = String.format(RedisKeyEnum.TABLE_ROWS_RECORD.getKey(), this.parentTable, shareTableInfo.getName());
            final long rows = Long.parseLong(Objects.requireNonNull(redisTemplate.opsForValue().increment(key)).toString());
            // 该表记录数小于等于max
            if (rows <= max) {
                // 将相同ds数据源、相同表名的插入数据放入同一个list
                final DataSourceTableMap dataSourceTableMap = DataSourceTableMap.builder().dataSource(ds).shareTableInfo(shareTableInfo).build();
                final List<T> beans = dsBeansMap.getOrDefault(dataSourceTableMap, new ArrayList<>());
                beans.add(data);
                dsBeansMap.put(dataSourceTableMap, beans);
            } else {
                // 分表记录数大于max
                log.debug("{}表记录数超过max:{}, 创建新的分片表", shareTableInfo.getName(), max);
                // 获取创建表的锁
                String lockName = String.format(RedisKeyEnum.CREATE_TABLE_LOCK.getKey(), this.parentTable);
                final Boolean getLock = redisTemplate.opsForValue().setIfAbsent(lockName, 1, Duration.ofMinutes(30));
                // 获取创建表锁成功
                if (getLock != null && getLock) {
                    // 创建表
                    String createTableSql = null;
                    try (Connection conn = ds.getConnection();
                         Statement statement = conn.createStatement()) {
                        // 查询父表所有信息
                        ParentTableInfoEntity parentTableInfo = parentTableInfoDao.selectByName(this.parentTable);
                        int count = parentTableInfo.getShareTableInfos().size();
                        // 新的分片表：父表名_分片数
                        String newShareTableName = this.parentTable + "_" + (count + 1);
                        createTableSql = SqlEnum.CREATE_TABLE_USER.getSql().replace(TABLE_NAME_PLACEHOLDER, newShareTableName);
                        // 执行创建表sql
                        statement.execute(createTableSql);
                        log.debug("创建表成功,sql={}", createTableSql);
                        // 插入分片表信息
                        shareTableInfoDao.insert(ShareTableInfoEntity.builder()
                                .createTime(new Date())
                                .updateTime(new Date())
                                .incrNum(count + 1)
                                .name(newShareTableName)
                                .parentTableId(parentTableInfo.getId())
                                .serverId(shareTableInfo.getServerEntity().getId())
                                .build());
                        // 对应分片表数据总数 + 1
                        parentTableInfoDao.updateIncrNumMax();
                    } catch (SQLException e) {
                        log.error("conn异常:{} ,sql={}", e.getMessage(), createTableSql, e);
                    } finally {
                        // 释放创建表锁
                        redisTemplate.delete(lockName);
                    }
                }

                // 将该sql放入队列
                redisTemplate.opsForList().leftPush(this.sqlQueue, JSON.toJSONString(data));
            }
        }

        // 对dsBeansMap进行分组批量操作（分组标识：数据源与分表信息）
        for (Map.Entry<DataSourceTableMap, List<T>> entry : dsBeansMap.entrySet()) {
            final DataSourceTableMap dataSourceTableMap = entry.getKey();
            // 获取数据源
            final DataSource ds = dataSourceTableMap.getDataSource();
            // 获取分表信息
            final ShareTableInfoEntity shareTableInfo = dataSourceTableMap.getShareTableInfo();
            // 获取待新增数据列表
            final List<T> list = entry.getValue();
            // 获取执行新增的sql语句
            final String newSql = SqlEnum.INSERT_USER.getSql().replace(TABLE_NAME_PLACEHOLDER, shareTableInfo.getName());
            try (Connection conn = ds.getConnection();
                 PreparedStatement preStatement = conn.prepareStatement(newSql)) {
                // 事务开启，设置成手动提交
                conn.setAutoCommit(false);
                for (T data : list) {
                    this.parseObjToPreparedStatement(preStatement, data);
                    // 将执行语句添加到缓冲区
                    preStatement.addBatch();
                }
                // 批量执行
                final int[] affectRows = preStatement.executeBatch();
                // 事务提交
                conn.commit();
                // 更新对应分片信息
                shareTableInfoDao.incrRowNumById(shareTableInfo.getId(), affectRows.length);
            } catch (SQLException e) {
                log.error("conn异常:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 单条插入
     *
     * @param singleData 单条数据
     */
    public void insert(T singleData) {
        // 默认分表策略:根据父表名查询最新的分片表信息（每条数据插入表时，根据策略选取不同的数据源）
        final ShareTableInfoEntity shareTableInfo = shareTableInfoDao.getLatestShareTableInfoByParentTableName(this.parentTable);
        // 获取数据源名称
        final String dsName = shareTableInfo.getServerEntity().getDsName();
        // 根据数据源名称从Spring容器获取对应的数据源Bean
        final DataSource ds = applicationContext.getBean(dsName, DataSource.class);
        // 获取分表最大限制行数
        final int max = shareTableInfo.getMaxRows();
        // 获取分表记录数
        String key = String.format(RedisKeyEnum.TABLE_ROWS_RECORD.getKey(), this.parentTable, shareTableInfo.getName());
        final long rows = Long.parseLong(Objects.requireNonNull(redisTemplate.opsForValue().increment(key)).toString());
        final String newSql = SqlEnum.INSERT_USER.getSql().replace(TABLE_NAME_PLACEHOLDER, shareTableInfo.getName());
        // 该表记录数小于等于max
        if (rows <= max) {
            try (Connection conn = ds.getConnection();
                 PreparedStatement preStatement = conn.prepareStatement(newSql)) {
                this.parseObjToPreparedStatement(preStatement, singleData);
                preStatement.execute();
                // 更新对应分片信息
                shareTableInfoDao.incrRowNumById(shareTableInfo.getId(), 1);
            } catch (SQLException e) {
                log.error("conn异常:{}", e.getMessage(), e);
            }
        } else {
            // 分表记录数大于max
            log.debug("{}表记录数超过max:{}, 创建新的分片表", shareTableInfo.getName(), max);
            // 获取创建表的锁
            String lockName = String.format(RedisKeyEnum.CREATE_TABLE_LOCK.getKey(), this.parentTable);
            final Boolean getLock = redisTemplate.opsForValue().setIfAbsent(lockName, 1, Duration.ofMinutes(30));
            // 获取创建表锁成功
            if (getLock != null && getLock) {
                // 创建表
                String createTableSql = null;
                try (Connection conn = ds.getConnection();
                     Statement statement = conn.createStatement()) {
                    // 查询父表所有信息
                    ParentTableInfoEntity parentTableInfo = parentTableInfoDao.selectByName(this.parentTable);
                    int count = parentTableInfo.getShareTableInfos().size();
                    // 新的分片表：父表名_分片数
                    String newShareTableName = this.parentTable + "_" + (count + 1);
                    createTableSql = SqlEnum.CREATE_TABLE_USER.getSql().replace(TABLE_NAME_PLACEHOLDER, newShareTableName);
                    // 执行创建表sql
                    statement.execute(createTableSql);
                    log.debug("创建表成功,sql={}", createTableSql);
                    // 插入分片表信息
                    shareTableInfoDao.insert(ShareTableInfoEntity.builder()
                            .createTime(new Date())
                            .updateTime(new Date())
                            .incrNum(count + 1)
                            .name(newShareTableName)
                            .parentTableId(parentTableInfo.getId())
                            .serverId(shareTableInfo.getServerEntity().getId())
                            .build());
                    // 对应分片表数据总数 + 1
                    parentTableInfoDao.updateIncrNumMax();
                } catch (SQLException e) {
                    log.error("conn异常:{} ,sql={}", e.getMessage(), createTableSql, e);
                } finally {
                    // 释放创建表锁
                    redisTemplate.delete(lockName);
                }
            }

            // 将该sql放入队列
            redisTemplate.opsForList().leftPush(this.sqlQueue, JSON.toJSONString(singleData));
        }
    }

    /**
     * 将对象属性设置到PreparedStatement语句中
     *
     * @param preStatement
     * @param data
     * @throws SQLException
     */
    abstract void parseObjToPreparedStatement(PreparedStatement preStatement, T data) throws SQLException;

    /**
     * 查询
     *
     * @param sql 查询sql
     */
    public void query(String sql) {
        final ArrayList<SqlExecuteInfo> sqlExecuteInfos = new ArrayList<>();
        final ArrayList<UserEntity> allDatas = new ArrayList<>();

        // 根据表名获取所有分片表对应的分片表信息与对应服务器信息
        final List<ShareTableInfoEntity> shareTableInfos = shareTableInfoDao.getShareTableInfoByParentTableName(parentTable);

        // 根据数据源信息，从Spring容器获取对应的数据源Bean
        for (ShareTableInfoEntity shareTableInfo : shareTableInfos) {
            // 获取数据源名称
            final String dsName = shareTableInfo.getServerEntity().getDsName();
            // 根据数据源名称从Spring容器获取对应的数据源Bean
            final DruidDataSource ds = applicationContext.getBean(dsName, DruidDataSource.class);
            // 封装执行sql信息体
            sqlExecuteInfos.add(SqlExecuteInfo.builder()
                    .dataSource(ds)
                    .shareTableInfo(shareTableInfo)
                    .build());
        }

        if (CollectionUtils.isEmpty(sqlExecuteInfos)) {
            return;
        }

        // 针对select * from table where
        for (SqlExecuteInfo sqlExecuteInfo : sqlExecuteInfos) {
            try (Connection conn = sqlExecuteInfo.getDataSource().getConnection();
                 Statement statement = conn.createStatement()) {
                final ShareTableInfoEntity shareTableInfo = sqlExecuteInfo.getShareTableInfo();
                // 将占位符替换成要查询的分表名称，获取真正执行的sql语句
                final String newSql = sql.replace("{table}", shareTableInfo.getName());
                // 执行查询sql语句
                final ResultSet resultSet = statement.executeQuery(newSql);
                // 将查询结果集resultSet转换成JavaBean
                final List<UserEntity> objects = JdbcUtil.resultSetToBean(resultSet, UserEntity.class);
                // 将最终查询结果添加到总查询集中
                allDatas.addAll(objects);
                // TODO: 2022/4/21 order by、limit、group by处理 
                log.debug("tableName={} 条数={}", shareTableInfo.getName(), objects.size());
            } catch (SQLException e) {
                log.info("conn异常:{}", e.getMessage(), e);
            } catch (Exception e) {
                log.info("resultSetToBean异常:{}", e.getMessage(), e);
            }
        }

        log.debug("查询成功: size={} tableSize:{}", allDatas.size(), sqlExecuteInfos.size());
    }

    /**
     * 删除
     *
     * @param sql sql语句
     */
    public void delete(String sql) {
        final ArrayList<SqlExecuteInfo> sqlExecuteInfos = new ArrayList<>();

        // 根据表名获取所有分片表对应的分片表信息与对应服务器信息
        final List<ShareTableInfoEntity> shareTableInfos = shareTableInfoDao.getShareTableInfoByParentTableName(parentTable);

        // 根据数据源信息，从Spring容器获取对应的数据源Bean
        for (ShareTableInfoEntity shareTableInfo : shareTableInfos) {
            // 获取数据源名称
            final String dsName = shareTableInfo.getServerEntity().getDsName();
            // 根据数据源名称从Spring容器获取对应的数据源Bean
            final DruidDataSource ds = applicationContext.getBean(dsName, DruidDataSource.class);
            // 封装执行sql信息体
            sqlExecuteInfos.add(SqlExecuteInfo.builder()
                    .dataSource(ds)
                    .shareTableInfo(shareTableInfo)
                    .build());
        }

        if (CollectionUtils.isEmpty(sqlExecuteInfos)) {
            return;
        }

        int deleteNum = 0;
        String newSql = null;
        for (SqlExecuteInfo sqlExecuteInfo : sqlExecuteInfos) {
            try (Connection conn = sqlExecuteInfo.getDataSource().getConnection();
                 Statement statement = conn.createStatement()) {
                final ShareTableInfoEntity shareTableInfo = sqlExecuteInfo.getShareTableInfo();
                newSql = sql.replace("{table}", shareTableInfo.getName());
                final int affectRows = statement.executeUpdate(newSql);
                if (affectRows > 0) {
                    // 对应分片表数据总数 - affectRows
                    shareTableInfoDao.incrRowNumById(sqlExecuteInfo.getShareTableInfo().getId(), -affectRows);
                    deleteNum += affectRows;
                }
            } catch (SQLException e) {
                log.info("conn异常:{}", e.getMessage(), e);
            } catch (Exception e) {
                log.info("resultSetToBean异常:{}", e.getMessage(), e);
            }
        }
        log.info("删除成功, sql={} 删除数={}", newSql, deleteNum);
    }

    /**
     * 更新
     *
     * @param sql sql语句
     */
    public void update(String sql) {
        final ArrayList<SqlExecuteInfo> sqlExecuteInfos = new ArrayList<>();

        // 根据表名获取所有分片表对应的分片表信息与对应服务器信息
        final List<ShareTableInfoEntity> shareTableInfos = shareTableInfoDao.getShareTableInfoByParentTableName(parentTable);

        // 根据数据源信息，从Spring容器获取对应的数据源Bean
        for (ShareTableInfoEntity shareTableInfo : shareTableInfos) {
            // 获取数据源名称
            final String dsName = shareTableInfo.getServerEntity().getDsName();
            // 根据数据源名称从Spring容器获取对应的数据源Bean
            final DruidDataSource ds = applicationContext.getBean(dsName, DruidDataSource.class);
            // 封装执行sql信息体
            sqlExecuteInfos.add(SqlExecuteInfo.builder()
                    .dataSource(ds)
                    .shareTableInfo(shareTableInfo)
                    .build());
        }

        if (CollectionUtils.isEmpty(sqlExecuteInfos)) {
            return;
        }

        int updateNum = 0;
        String newSql = null;
        for (SqlExecuteInfo sqlExecuteInfo : sqlExecuteInfos) {
            try (Connection conn = sqlExecuteInfo.getDataSource().getConnection();
                 Statement statement = conn.createStatement()) {
                final ShareTableInfoEntity shareTableInfo = sqlExecuteInfo.getShareTableInfo();
                newSql = sql.replace("{table}", shareTableInfo.getName());
                final int affectRows = statement.executeUpdate(newSql);
                updateNum += affectRows;
            } catch (SQLException e) {
                log.info("conn异常:{}", e.getMessage(), e);
            } catch (Exception e) {
                log.info("resultSetToBean异常:{}", e.getMessage(), e);
            }
        }
        log.info("更新成功, sql={} 更新数={}", newSql, updateNum);
    }

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SqlExecuteInfo {
    private DataSource dataSource;
    private ShareTableInfoEntity shareTableInfo;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class BatchSqlExecuteInfo {

    private ShareTableInfoEntity shareTableInfo;

    private UserEntity user;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DataSourceTableMap {
    private DataSource dataSource;
    private ShareTableInfoEntity shareTableInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataSourceTableMap that = (DataSourceTableMap) o;
        return Objects.equals(dataSource, that.dataSource) && Objects.equals(shareTableInfo, that.shareTableInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSource, shareTableInfo);
    }
}