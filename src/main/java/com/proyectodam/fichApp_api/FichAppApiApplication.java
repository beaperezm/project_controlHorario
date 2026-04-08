package com.proyectodam.fichApp_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada principal de la API REST para FichApp.
 * Inicializa el contexto de Spring Boot y la configuración automática.
 */
@SpringBootApplication
public class FichAppApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FichAppApiApplication.class, args);
    }
}
