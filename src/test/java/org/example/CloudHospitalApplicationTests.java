package org.example;


import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.ShareTableInfoDao;
//import org.example.dao.UserDao;
import org.example.dao.BaseDao;
import org.example.dao.UserDao;
import org.example.entity.*;
import org.example.mapper.MedicalFileJsonContentMapper;
import org.example.service.TestService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

@Slf4j
@SpringBootTest
class CloudHospitalApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private TestService testService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private MedicalFileJsonContentMapper mapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ShareTableInfoDao shareTableInfoDao;
    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private UserDao userDao;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class DataSourceTableMap {
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

    @Test
    void test01() throws IOException {
//        final ArrayList<UserEntity> userEntities = new ArrayList<>();
//        for (int i = 0; i < 500; i++) {
//            final UserEntity userEntity = new UserEntity();
//            userEntity.setUserName(RandomUtil.randomString(8));
//            userEntity.setNickName("管理员 第" + i);
//            userEntity.setPasswd("e18c8c2ed47ba5da882acada637ec9a60ec19461bc1d66d651cc69256794b26a");
//            userEntity.setDepartment("department");
//            userEntity.setPhone(RandomUtil.randomString(11));
//            userEntity.setName(RandomUtil.randomString(10));
//            userEntity.setState(1);
//            userEntity.setLastTime(new Date());
//            userEntity.setUpdatedTime(new Date());
//            userEntity.setCreatedTime(new Date());
//            userEntity.setSalt("adminpass");
//            userEntity.setPwdIsChanged(true);
//            userEntities.add(userEntity);
//        }
//        baseDao.insertBatch(userEntities);

        for (int i = 0; i < 10; i++) {
            final UserEntity userEntity = new UserEntity();
            userEntity.setUserName(RandomUtil.randomString(8));
            userEntity.setNickName("管理员 第" + i);
            userEntity.setPasswd("e18c8c2ed47ba5da882acada637ec9a60ec19461bc1d66d651cc69256794b26a");
            userEntity.setDepartment("department");
            userEntity.setPhone(RandomUtil.randomString(11));
            userEntity.setName(RandomUtil.randomString(10));
            userEntity.setState(1);
            userEntity.setLastTime(new Date());
            userEntity.setUpdatedTime(new Date());
            userEntity.setCreatedTime(new Date());
            userEntity.setSalt("adminpass");
            userEntity.setPwdIsChanged(true);

            baseDao.insert(userEntity);
        }
//        final HashMap<ShareTableInfoEntity, List<Integer>> hm = new HashMap<>();
//        // 第一次
//        final ShareTableInfoEntity shareTableInfoEntity = new ShareTableInfoEntity();
//        shareTableInfoEntity.setId(1);
//        final List<Integer> list1 = hm.getOrDefault(shareTableInfoEntity, new ArrayList<>());
//        list1.add(1);
//        hm.put(shareTableInfoEntity, list1);
//        // 第二次
//        final ShareTableInfoEntity shareTableInfoEntity2 = new ShareTableInfoEntity();
//        shareTableInfoEntity2.setId(1);
//        final List<Integer> list2 = hm.getOrDefault(shareTableInfoEntity2, new ArrayList<>());
//        list2.add(2);
//        hm.put(shareTableInfoEntity2, list2);

//        final HashMap<DataSourceTableMap, List<Integer>> hm = new HashMap<>();
//        // 第一次
//        final DruidDataSource ds1 = applicationContext.getBean("ds1", DruidDataSource.class);
//        final ShareTableInfoEntity shareTableInfoEntity = new ShareTableInfoEntity();
//        shareTableInfoEntity.setId(1);
//        final DataSourceTableMap dst1 = DataSourceTableMap.builder().dataSource(ds1).shareTableInfo(shareTableInfoEntity).build();
//        final List<Integer> list1 = hm.getOrDefault(dst1, new ArrayList<>());
//        list1.add(1);
//        hm.put(dst1, list1);
//        // 第二次
//        final DruidDataSource ds2 = applicationContext.getBean("ds1", DruidDataSource.class);
//        final ShareTableInfoEntity shareTableInfoEntity2 = new ShareTableInfoEntity();
//        shareTableInfoEntity2.setId(1);
//        final DataSourceTableMap dst2 = DataSourceTableMap.builder().dataSource(ds2).shareTableInfo(shareTableInfoEntity2).build();
//        final List<Integer> list2 = hm.getOrDefault(dst2, new ArrayList<>());
//        list2.add(2);
//        hm.put(dst2, list2);

//        final HashMap<DataSource, List<Integer>> hm = new HashMap<>();
//        // 第一次
//        final DruidDataSource ds1 = applicationContext.getBean("ds1", DruidDataSource.class);
//        final List<Integer> list1 = hm.getOrDefault(ds1, new ArrayList<>());
//        list1.add(1);
//        hm.put(ds1, list1);
//        // 第二次
//        final DruidDataSource ds2 = applicationContext.getBean("ds1", DruidDataSource.class);
//        final List<Integer> list2 = hm.getOrDefault(ds2, new ArrayList<>());
//        list2.add(2);
//        hm.put(ds2, list2);

        System.out.println(">....");

//        userDao.query("select * from {table}");
//        userDao.update("update {table} set nick_name= 'heihei' where id in (109, 116) ");
//        userDao.delete("delete from {table} where user_name = 'adminbvnwf第1_user02'");
//        final List<ShareTableInfoEntity> user = shareTableInfoDao.getServerInfoByParentTableName("user");
//        final List<ShareTableInfoEntity> user = shareTableInfoDao.getServerInfoByParentTableName("user");
//        log.info("{}", user);
//        userDao.query("select * from {table} where phone = '13660670642'");
//        for (int i = 0; i < 10; i++) {
//            userDao.insert("INSERT INTO {table}\n" +
//                    "(id, user_name, nick_name, passwd, department, phone, name, state, last_time, created_time, updated_time, salt, pwd_is_changed)\n" +
//                    String.format("VALUES({id}, 'admin%s第%d_user02', '管理员', 'e18c8c2ed47ba5da882acada637ec9a60ec19461bc1d66d651cc69256794b26a', NULL, '%s', NULL, 1, '2022-03-23 17:54:14', '2020-09-07 14:12:08', '2022-03-23 17:54:14', 'adminpass', 1);\n", RandomUtil.randomString(5), i, RandomUtil.randomInt(11000, 200000)));
//        }
//        jdbcTemplate.execute("insert  into employees (id,fname,lname,hired,store_id) values(null,'张三','张','2015-05-04',2);");
//        RLock reportReadyLock = redissonClient.getLock("test-redission-lock");
//        try {
//            // 获取准备发送报告锁
//            boolean res = reportReadyLock.tryLock(5, 10, TimeUnit.SECONDS);
//            if (res) {
//                log.info("获取锁成功");
//            }
//
//        } catch (InterruptedException e) {
//            //处理
//            //保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应
//            Thread.currentThread().interrupt();
//            log.error("中断异常", e);
//        } finally {
//            log.info("reportReadyLock.isLocked():{}", reportReadyLock.isLocked());
//            log.info("reportReadyLock.isHeldByCurrentThread():{}", reportReadyLock.isHeldByCurrentThread());
//            reportReadyLock.unlock();
//        }
//        try (SqlSession sqlSession = SqlSessionUtils.getSqlSession(
//                sqlSessionTemplate.getSqlSessionFactory(),
//                ExecutorType.BATCH, sqlSessionTemplate.getPersistenceExceptionTranslator())) {
//            final MedicalFileJsonContentMapper batchMapper = sqlSession.getMapper(MedicalFileJsonContentMapper.class);
//
//            for (int i = 0; i < 10; i++) {
//                if (i == 7) {
//                    batchMapper.insert(MedicalFileJsonContentEntity.builder()
//                            .id(1L)
//                            .fileJson("name" + i).fileJson("s" + i)
//                            .createTime(new Date())
//                            .fileType(1)
//                            .updateTime(new Date())
//                            .reportId((long) i)
//                            .build());
//                } else {
//                    batchMapper.insert(MedicalFileJsonContentEntity.builder()
//                            .fileJson("name" + i).fileJson("s" + i)
//                            .createTime(new Date())
//                            .fileType(1)
//                            .updateTime(new Date())
//                            .reportId((long) i)
//                            .build());
//                }
//            }
//
//            // 刷新批处理语句，且执行缓存中还没执行的SQL语句
//            sqlSession.flushStatements();
//            sqlSession.commit();
//        }

        // 预热
//        mysqlSingle(500);
//        mysqlBatch(5);
//        mysqlSingle(500);

        // 预热
//        redisSingle(500);
//        redisBatch(500);
//        redisSingle(500);
    }


//    public void mysqlSingle(int num) {
//        final long l = System.currentTimeMillis();
//        for (int i = 0; i < num; i++) {
//            mapper.insert(MedicalFileJsonContentEntity.builder().fileJson("{\"age\":\"11\",\"cardNo\":\"1234567890123\",\"cardType\":\"身份证\",\"checkResult\":\"阴性\",\"checkUnit\":\"检测机构\",\"collectTime\":\"2022-03-09 17:43:21\",\"collectUnit\":\"采样点名称\",\"fileType\":\"pdf\",\"id\":\"126342132113120\",\"institution\":\"hafypt2022\",\"mobile\":\"15910000000\",\"name\":\"梁健林\",\"templateType\":\"hsjc001\",\"timestamp\":\"1111\"}").fileType(2).createTime(new Date()).updateTime(new Date()).reportId(IdUtil.getSnowflake().nextId()).build());
//        }
//        log.info("【mysql】多次耗时:{}ms", System.currentTimeMillis() - l);
//    }
//
//    public void mysqlBatch(int num) {
//        // 批量执行
//        final long l = System.currentTimeMillis();
//        try (SqlSession sqlSession = SqlSessionUtils.getSqlSession(
//                sqlSessionTemplate.getSqlSessionFactory(),
//                ExecutorType.BATCH, sqlSessionTemplate.getPersistenceExceptionTranslator())) {
//            final MedicalFileJsonContentMapper batchMapper = sqlSession.getMapper(MedicalFileJsonContentMapper.class);
//            for (int i = 0; i < num; i++) {
//                batchMapper.insert(MedicalFileJsonContentEntity.builder().fileJson("{\"age\":\"11\",\"cardNo\":\"1234567890123\",\"cardType\":\"身份证\",\"checkResult\":\"阴性\",\"checkUnit\":\"检测机构\",\"collectTime\":\"2022-03-09 17:43:21\",\"collectUnit\":\"采样点名称\",\"fileType\":\"pdf\",\"id\":\"126342132113120\",\"institution\":\"hafypt2022\",\"mobile\":\"15910000000\",\"name\":\"梁健林\",\"templateType\":\"hsjc001\",\"timestamp\":\"1111\"}").fileType(2).createTime(new Date()).updateTime(new Date()).reportId(IdUtil.getSnowflake().nextId()).build());
//            }
//
//            log.info("故意执行错");
//            batchMapper.insert(MedicalFileJsonContentEntity.builder().fileJson("{\"age\":\"11\",\"cardNo\":\"1234567890123\",\"cardType\":\"身份证\",\"checkResult\":\"阴性\",\"checkUnit\":\"检测机构\",\"collectTime\":\"2022-03-09 17:43:21\",\"collectUnit\":\"采样点名称\",\"fileType\":\"pdf\",\"id\":\"126342132113120\",\"institution\":\"hafypt2022\",\"mobile\":\"15910000000\",\"name\":\"梁健林\",\"templateType\":\"hsjc001\",\"timestamp\":\"1111\"}").fileType(2).updateTime(new Date()).reportId(IdUtil.getSnowflake().nextId()
//            ).id(1L).build());
//
//
//            // 刷新批处理语句，且执行缓存中还没执行的SQL语句
//            sqlSession.flushStatements();
//            sqlSession.commit();
//        }
//        log.info("【mysql】批量耗时:{}ms", System.currentTimeMillis() - l);
//    }
//
//    public void redisSingle(int num) {
//        final long l = System.currentTimeMillis();
//        for (int i = 0; i < num; i++) {
//            redisTemplate.opsForValue().set(("key:keykk" + i), "1".getBytes());
//        }
//        log.info("【redis】多次耗时:{}ms", System.currentTimeMillis() - l);
//    }
//
//    public void redisBatch(int num) {
//        final long l = System.currentTimeMillis();
//        redisTemplate.executePipelined(new RedisCallback<String>() {
//            @Override
//            public String doInRedis(RedisConnection connection) throws DataAccessException {
//                for (int i = 0; i < num; i++) {
//                    connection.set(("pipel:" + i).getBytes(), "1".getBytes());
//                }
//                return null;
//            }
//        });
//        log.info("【redis】批量耗时:{}ms", System.currentTimeMillis() - l);
//    }
}
