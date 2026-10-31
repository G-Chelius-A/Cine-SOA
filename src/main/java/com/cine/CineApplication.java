package com.cine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;

@SpringBootApplication
public class CineApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CineApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(new String[0]);
    }
}
