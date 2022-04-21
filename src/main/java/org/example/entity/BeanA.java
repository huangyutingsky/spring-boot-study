package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/11 13:52
 */
@Data
//@Builder
//@AllArgsConstructor
@NoArgsConstructor
public class BeanA<T> {
    private String name;
    private int age;
    private int sex;
    private T custom;
}
