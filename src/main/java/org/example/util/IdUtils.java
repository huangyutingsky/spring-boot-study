package org.example.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author liangjianlin
 * @version 1.0
 * @date 2022/3/17 10:13
 */
@Slf4j
@Component
public class IdUtils {

    /**
     * 终端ID
     */
    private static long workId;

    /**
     * 数据中心ID
     */
    private static long datacenterId;


    private IdUtils() {
    }

    /**
     * 获取随机id
     *
     * @return
     */
    public static Long getSnowFlakeId() {
        Snowflake snowflake = IdUtil.getSnowflake(2, 2);
        return snowflake.nextId();
    }

}
