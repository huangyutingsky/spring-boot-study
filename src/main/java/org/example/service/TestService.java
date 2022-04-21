package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/21 15:27
 */
@Slf4j
@Service
public class TestService {

    @Async
    public String test(){
        log.info("你好：{}", Thread.currentThread().getName());
        return Thread.currentThread().getName();
    }
}
