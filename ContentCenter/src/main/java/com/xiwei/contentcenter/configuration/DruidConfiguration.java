package com.xiwei.contentcenter.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DruidConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource getDataSource() {
        return new DruidDataSource();
    }
}
