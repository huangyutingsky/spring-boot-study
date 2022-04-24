package org.example.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/24 17:47
 */
public class JdbcUtil {

    private static final Pattern DATE_FIELD_PATTERN = Pattern.compile(".*Time$");

    public static <T> List<T> resultSetToBean(ResultSet resultSet, Class beanClass) throws Exception {
        // 获取Bean对象内的所有属性
        Field[] fields = beanClass.getDeclaredFields();
        List<T> beanList = new ArrayList<>();
        if (resultSet != null) {
            while (resultSet.next()) {
                // 每当有一行数据就创建一个Bean对象
                T object = (T) beanClass.newInstance();
                for (Field field : fields) {
                    String fieldName = field.getName();
                    if ("serialVersionUID".equals(fieldName)) {
                        continue;
                    }
                    // 利用字符串拼接，将属性名的首字母变为大写，获取对应的set方法。
                    Method setField = beanClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
                    Matcher matcher = DATE_FIELD_PATTERN.matcher(fieldName);
                    // 这里对Date类型的字段赋值进行区分赋值，直接赋值会报错
                    if (!matcher.matches()) {
                        setField.invoke(object, resultSet.getObject(toUnderlineCase(fieldName)));
                    } else {
                        setField.invoke(object, new Date(resultSet.getTimestamp(toUnderlineCase(fieldName)).getTime()));
                    }
                }
                beanList.add(object);
            }
        }
        return beanList;
    }

    /**
     * 驼峰转 下划线
     * userName  ---->  user_name
     * user_name  ---->  user_name
     *
     * @param camelCaseStr 驼峰字符串
     * @return 带下滑线的String
     */
    public static String toUnderlineCase(String camelCaseStr) {
        if (camelCaseStr == null) {
            return null;
        }
        // 将驼峰字符串转换成数组
        char[] charArray = camelCaseStr.toCharArray();
        StringBuffer buffer = new StringBuffer();
        //处理字符串
        for (int i = 0, l = charArray.length; i < l; i++) {
            if (charArray[i] >= 65 && charArray[i] <= 90) {
                buffer.append("_").append(charArray[i] += 32);
            } else {
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }
}
