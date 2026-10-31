package com.cine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// CORRECCIÓN [116]: Habilitar servidor web de Spring Boot eliminando WebApplicationType.NONE | MOTIVO: CineApplication tenía configurado explícitamente application.setWebApplicationType(WebApplicationType.NONE), lo que apagaba el servidor web inmediatamente tras iniciar | ANTES: application.setWebApplicationType(WebApplicationType.NONE); application.run(new String[0]);
@SpringBootApplication
public class CineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineApplication.class, args);
    }

}
