package com.qgao.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * qgao
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ArticleMain8001 {
    public static void main(String[] args){
        SpringApplication.run(ArticleMain8001.class, args);
    }
}
