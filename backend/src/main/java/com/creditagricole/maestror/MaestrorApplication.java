package com.creditagricole.maestror;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaestrorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaestrorApplication.class, args);
    }
}
