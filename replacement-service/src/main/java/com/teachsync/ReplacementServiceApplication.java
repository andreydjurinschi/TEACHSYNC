package com.teachsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReplacementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReplacementServiceApplication.class, args);
    }

}
