package com.xiwei.contentcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * name值一定要使用服务端配置的服务名（spring.application.name），通过configuration指定自定义的配置
 */
@MapperScan("com.xiwei")
@SpringBootApplication
@EnableDiscoveryClient
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }
}
