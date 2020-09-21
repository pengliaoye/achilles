package com.getxinfo.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class AchillesConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(AchillesConfigApplication.class, args);
    }

}
