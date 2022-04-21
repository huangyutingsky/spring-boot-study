package org.example.main;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/8 9:39
 */
@Slf4j
public class ThreadTest {
    public static void main(String[] args) throws IOException {
        Files.delete(Paths.get("D:\\test-dir"));
    }
}
