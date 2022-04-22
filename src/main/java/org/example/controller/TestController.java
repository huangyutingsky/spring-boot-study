package org.example.controller;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.example.dao.UserDao;
import org.example.entity.MyProperty;
import org.example.service.caiyun.CaiyunCreateCatalogExtApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/16 15:14
 */
@Slf4j
@RestController
public class TestController {

    @Value("${my.name}")
    private String name;

    @Autowired
    private MyProperty property;
    @Autowired
    private UserDao userDao;

    @Autowired
    private CaiyunCreateCatalogExtApi caiyunCreateCatalogExtApi;

    @RequestMapping("Test01")
    public Map<String, Object> test01() {
        for (int i = 0; i < 10; i++) {
            userDao.insert("INSERT INTO {table}\n" +
                    "(id, user_name, nick_name, passwd, department, phone, name, state, last_time, created_time, updated_time, salt, pwd_is_changed)\n" +
                    String.format("VALUES({id}, 'admin%s第%d_user02', '管理员', 'e18c8c2ed47ba5da882acada637ec9a60ec19461bc1d66d651cc69256794b26a', NULL, '%s', NULL, 1, '2022-03-23 17:54:14', '2020-09-07 14:12:08', '2022-03-23 17:54:14', 'adminpass', 1);\n", RandomUtil.randomString(5), i, RandomUtil.randomInt(11000, 200000)));
        }
        return null;
    }

    @RequestMapping("Test02")
    public Map<String, Object> test02() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String command = "rm -rf %s";
        String body = "/opt/data/software/testDir";
        if("/*".equals(body) || StringUtils.isEmpty(body)){
            log.info("命令非法:command={}, body={}", command, body);
            return null;
        }else {
            rt.exec(String.format(command, body));
            log.info("命令执行成功: command={}, body={}", command, body);
        }
        return null;
    }


    /**
     * 获取项目所在挂载盘符空间使用情况
     *
     * @return
     */
    public static void diskUsage() throws IOException {
        //  获取项目所在挂在盘
        String drive = getMountOfProject();

        int used = 0;
        int total = 0;
        try {
            Runtime rt = Runtime.getRuntime();
            //执行 df 命令查看所以挂载盘符空间使用情况
            Process p = rt.exec("df -B 1g");
            List<String> result = IOUtils.readLines(p.getInputStream(), StandardCharsets.UTF_8);
            log.info("执行命令:df -B 1g 返回:{}", result);
            for (String line : result) {
                final String[] items = line.split("\\s+");
                String driveName = items[5];
                log.info("要查询的盘符:{} 遍历drive:{} 是否匹配:{}", drive, driveName, drive.equals(driveName));
                // 根据指定盘符名称，获取该盘符空间使用情况
                if (drive.equals(driveName)) {
                    log.info("查询的磁盘名称:{} 磁盘信息:{}", drive, line);
                    // 以一个或多个空格来分割字符串
                    total = Integer.parseInt(items[1]);
                    used = Integer.parseInt(items[2]);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("查询磁盘空间diskUsage方法异常:{}, 请求参数: drive={}", e.getMessage(), drive, e);
        }

    }

    /**
     * 获取项目所在挂在盘 （如: /opt）
     *
     * @return
     * @throws IOException
     */
    public static String getMountOfProject() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String projectPath = new File(".").getCanonicalPath();
        log.info("项目路径:{}", projectPath);
        Process p = rt.exec("df -h " + projectPath);
        List<String> result = IOUtils.readLines(p.getInputStream(), StandardCharsets.UTF_8);
        log.info("获取到挂载盘信息");
        for (String s : result) {
            log.info("{}", s);
        }
        final String[] items = result.get(1).split("\\s+");
        log.info("当前项目所在挂载盘名称:{}", items[5]);
        return items[5];
    }
}
