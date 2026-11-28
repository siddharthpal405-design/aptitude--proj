package com.aptitude.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AptitudePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AptitudePlatformApplication.class, args);
    }
}
