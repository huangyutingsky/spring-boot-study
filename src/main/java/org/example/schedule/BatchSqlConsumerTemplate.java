package org.example.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangyuting
 * @Description: 定时消费批量操作SQL模板类：
 * 批量操作逻辑：
 * 1、优先积攒BATCH_MAX_SIZ条sql，使用sql事务批量执行
 * 2、如果批次数量不足BATCH_MAX_SIZ条，则直接执行
 * @date 2021/11/12 16:11
 */
@Slf4j
@Component
@EnableScheduling
public class BatchSqlConsumerTemplate {

    /**
     * 批量操作SQL最大记录数
     */
    private static final int BATCH_MAX_SIZE = 500;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     *
     * @param queue
     * @param batchSqlExecution
     */
    protected void batchSqlExecution(String queue, BatchSqlExecutionInterface batchSqlExecution){
        final Object obj = redisTemplate.opsForList().rightPop(queue);
        if (obj == null) {
            return;
        }

        final List<Object> messages = new ArrayList<>();
        messages.add(obj);

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                while (true) {
                    final Object obj = operations.opsForList().rightPop(queue);
                    if (obj != null) {
                        messages.add(obj);
                        // 如果数组大于等于BATCH_MAX_SIZE，退出while循环
                        if (messages.size() >= BATCH_MAX_SIZE) {
                            break;
                        }
                    } else {
                        // pop返回为空，代表队列为空，退出while循环
                        break;
                    }
                }
                return null;
            }
        });

        batchSqlExecution.sqlExecution(messages);
    }

}
