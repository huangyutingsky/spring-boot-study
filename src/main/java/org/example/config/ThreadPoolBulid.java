package org.example.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/21 15:25
 */
@Configuration
public class ThreadPoolBulid {

    /**
     * 线程名称前缀 ...
     */
    private static final String THREAD_NAME_PREFIX = "yunhospital-async";

    /**
     * 核心线程数 ...
     */
    @Value("5")
    private int corePoolSize;

    /**
     * 最大线程数 ...
     */
    @Value("50")
    private int maxPoolSize;

    /**
     * 队列长度 ...
     */
    @Value("1000")
    private int queueCapacity;

    /**
     * 线程存活时长 ...
     */
    @Value("5")
    private int keepAliveSeconds;

    @Bean("myPool")
    public Executor getThreadPool() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("my-pool-%d").build(); // 需引入guava依赖
        return new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }
}
