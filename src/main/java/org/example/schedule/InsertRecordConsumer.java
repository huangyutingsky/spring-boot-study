package org.example.schedule;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.BaseDao;
import org.example.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/22 11:22
 */
@Component
@Slf4j
@EnableScheduling
public class InsertRecordConsumer extends BatchSqlConsumerTemplate {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private BaseDao baseDao;

    private static final String PARENT_TABLE = "user";

    @Scheduled(cron = "0/1 * * * * ?")
    private void insertUserRecordConsumer() {

        // 判断创建表锁是否已释放（判断创建表动作是否已完成）
        String lockName = "createTableLock:" + PARENT_TABLE;
        final Boolean exist = redisTemplate.hasKey(lockName);
        if (exist) {
            // 创建表锁未释放，证明创建表动作未完成
            return;
        }

        // 自定义【批量执行sql具体逻辑方法】
        BatchSqlExecutionInterface batchSqlExecution = (messages) -> {
            final List<UserEntity> list = JSON.parseArray(messages.toString(), UserEntity.class);
            baseDao.insertBatch(list);
        };

        String queue = "queue:insertUserQueue";

        // 将【批量执行sql具体逻辑方法】作为入参，执行总的批量执行sql逻辑方法
        this.batchSqlExecution(queue, batchSqlExecution);
    }

}
