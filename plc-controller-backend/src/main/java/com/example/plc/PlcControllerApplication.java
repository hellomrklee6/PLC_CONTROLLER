package com.example.plc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlcControllerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlcControllerApplication.class, args);
    }
}