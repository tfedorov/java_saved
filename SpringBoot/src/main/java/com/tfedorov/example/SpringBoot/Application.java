package com.tfedorov.example.SpringBoot;

import com.tfedorov.example.SpringBoot.config.JDBCConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(new Class<?>[]{Application.class, JDBCConfig.class}, args);
    }
}
