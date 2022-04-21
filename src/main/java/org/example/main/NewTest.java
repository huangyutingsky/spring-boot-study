package org.example.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.example.entity.BeanA;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/11 13:53
 */
public class NewTest {
    public static void main(String[] args) throws IOException {
        FileUtils.deleteDirectory(new File("D:\\batch"));
    }
}
