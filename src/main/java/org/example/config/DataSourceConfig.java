package org.example.config;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/21 10:44
 */
@Configuration
public class DataSourceConfig {

    @Bean("ds1")
    public DataSource getDs1(){
        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/medical_report?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("admin28270768");
        dataSource.setDbType(DbType.mysql);
        return dataSource;
    }
}
