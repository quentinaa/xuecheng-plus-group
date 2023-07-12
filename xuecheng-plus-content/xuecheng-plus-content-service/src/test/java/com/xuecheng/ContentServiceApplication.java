package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author xiong
 * @version 1.0
 * @description 内容管理服务启动类
 * @date 2023/2/11 15:49
 */
@EnableFeignClients(basePackages={"com.xuecheng.content.feignclient"})
@SpringBootApplication
public class ContentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentServiceApplication.class, args);
    }
}
