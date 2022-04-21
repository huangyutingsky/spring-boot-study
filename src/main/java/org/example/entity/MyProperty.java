package org.example.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/2/16 15:15
 */
@Data
@Component
@ConfigurationProperties(prefix = "my")
public class MyProperty {
    private String name;
    private Integer age;
    private String sex;
}
