package com.qgao.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class NotifyMain9002 {

    public static void main(String[] args){
        SpringApplication.run(NotifyMain9002.class, args);
    }
}
