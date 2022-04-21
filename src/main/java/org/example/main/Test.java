package org.example.main;

import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.sun.deploy.util.SystemUtils;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.example.entity.Bean;
import org.example.entity.MyProperty;
import org.example.entity.Person;
import org.openjdk.jol.info.ClassLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.sound.midi.Instrument;
import java.beans.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/16 14:02
 */
@Slf4j
public class Test {
    @Autowired
    private Person person;

    // 需要注意的是，使用TTL的时候，要想传递的值不出问题，线程池必须得用TTL加一层代理（下面会讲这样做的目的）
//    private static Executor executorService = TtlExecutors.getTtlExecutor(Executors.newFixedThreadPool(2));

    private static ThreadLocal tl = new TransmittableThreadLocal<>(); //这里采用TTL的实现

    public Optional<String> test() {
        if (true) {
            return Optional.ofNullable(null);
        } else {
            return Optional.of(null);
        }
    }

    private static boolean stopRequested;

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("insufficient_disk_space".toUpperCase());
    }

    public static void testUrl(String urlString) throws IOException {
        InputStream in = null;
        URL url;
        try {
            url = new URL(urlString);
            in = url.openStream();
            log.info("连接可用:url={}", urlString);
        } catch (Exception e) {
            log.info("连接不可用:url={}", urlString);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }


    public static Runnable runRightBody(int start, int end) {
        return new Runnable() {
            @Override
            public void run() {
                for (int i = start; i < end; i++) {
                    File file = new File(String.format("D:\\data\\software\\mhp\\docker\\java\\file\\source\\test-right-%d.json", i));
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        byte[] bytes = new byte[1024 * 8];
                        fileOutputStream.write(getJson(i).getBytes(StandardCharsets.UTF_8));
                        fileOutputStream.flush();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                log.info("{}结束", Thread.currentThread().getName());
            }
        };
    }

    public static Runnable runErrorBody(int start, int end) {
        return new Runnable() {
            @Override
            public void run() {
                for (int i = start; i < end; i++) {
                    File file = new File(String.format("D:\\batch-error\\test-error-%d.json", i));
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        byte[] bytes = new byte[1024 * 8];
                        fileOutputStream.write(getErrorJson2(i).getBytes(StandardCharsets.UTF_8));
                        fileOutputStream.flush();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                log.info("{}结束", Thread.currentThread().getName());
            }
        };
    }

    public static String getJson(int i) {
        String jsonStr = "{\n" +
                "    \"name\": \"更新测试%d\",\n" +
                "        \"mobile\": \"15815950857\",\n" +
                "    \"id_card\": \"445321199506150619\",\n" +
                "    \"file_no\": \"TEST_0000youkenengfdawdddddaaaaaaaahuangy%d\",\n" +
                "    \"file_list\": [\n" +
                "        {\n" +
                "            \"file_name\": \"test.pdf\",\n" +
                "            \"file_path\": \"\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"file_count\": 1,\n" +
                "    \"dept\": \"体检中心\",\n" +
                "    \"level\": \"团体\",\n" +
                "    \"project_name\": \"体检报告\",\n" +
                "    \"checkup_time\": \"2019-12-26\",\n" +
                "    \"unit\": \"测试单位\"\n" +
                "}";
        return String.format(jsonStr, i, i);
    }

    public static String getErrorJson(int i) {
        String jsonStr = "{\n" +
                "    \"name\": \"TEST_%d\",\n" +
                "\t\"mobile\": \"15815950857\",\n" +
                "    \"id_card\": \"44532119950611101\",\n" +
                "    \"file_no\": \"TEST_0123dawdaw1333e%d\",\n" +
                "    \"file_list\": [\n" +
                "        {\n" +
                "            \"file_name\": \"test.pdf\",\n" +
                "            \"file_path\": \"\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"file_count\": 1,\n" +
                "    \"dept\": \"体检中心\",\n" +
                "    \"level\": \"团体\",\n" +
                "    \"project_name\": \"体检报告222222222222222222222222222222222222222222222222\",\n" +
                "    \"checkup_time\": \"2019-12-26\",\n" +
                "    \"unit\": \"单位\",\n" +
                "\t\"authorized\":\"N\",\n" +
                "\t\"authorized_start_time\":\"2022-02-09\",\n" +
                "\t\"authorized_end_time\":\"\",\n" +
                "\t\"business_id\": \"10333\"\n" +
                "}";
        return String.format(jsonStr, i, i);
    }

    public static String getErrorJson2(int i) {
        String jsonStr = "{\n" +
                "    \"name\": \"TEST_%d\",\n" +
                "\t\"mobile\": \"15815950857\",\n" +
                "    \"id_card\": \"44532119950611101\",\n" +
                "    \"file_no\": \"TEST_0123dawdaw1333e%d\",\n" +
                "    \"file_list\": [\n" +
                "        {\n" +
                "            \"file_name\": \"tesd2t.pdf\",\n" +
                "            \"file_path\": \"\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"file_count\": 1,\n" +
                "    \"dept\": \"体检中心\",\n" +
                "    \"level\": \"团体\",\n" +
                "    \"project_name\": \"体检报告222222222222222222222222222222222222222222222222\",\n" +
                "    \"checkup_time\": \"2019-12-26\",\n" +
                "    \"unit\": \"单位\",\n" +
                "\t\"authorized\":\"N\",\n" +
                "\t\"authorized_start_time\":\"2022-02-09\",\n" +
                "\t\"authorized_end_time\":\"\",\n" +
                "\t\"business_id\": \"10333\"\n" +
                "}";
        return String.format(jsonStr, i, i);
    }

}

class Driver {
    public static void main(String[] args) {
        ThreadWithStop threadWithStop = new ThreadWithStop();
        Thread thread = new Thread(threadWithStop);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadWithStop.stopThread();
    }
}

class ThreadWithStop implements Runnable {

    private boolean stop;

    public ThreadWithStop() {
        this.stop = false;
    }

    public void stopThread() {
        this.stop = true;
    }

    public boolean shouldRun() {
        return !this.stop;
    }

    @Override
    public void run() {
        long count = 0L;
        while (shouldRun()) {
            count++;
        }
        System.out.println(count+"Done!");
    }
}
