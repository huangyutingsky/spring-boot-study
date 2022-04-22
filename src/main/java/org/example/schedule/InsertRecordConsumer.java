package org.example.schedule;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.UserDao;
import org.example.entity.SqlExecuteMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/22 11:22
 */
@Component
@Slf4j
@EnableScheduling
public class InsertRecordConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserDao userDao;

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

        // 创建表锁已释放，证明创建表动作完成
        String queue = "queue:insertUserQueue";
        final Object o = redisTemplate.opsForList().rightPop(queue);
        if (o == null) {
            return;
        }

        final SqlExecuteMessage message = JSON.parseObject(o.toString(), SqlExecuteMessage.class);
        // 执行插入数据逻辑
        userDao.insert(message.getSql());

    }

}
