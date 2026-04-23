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
        // Buscar el archivo de configuración en varias rutas posibles según el entorno
        // de ejecución
        java.io.File envFile = new java.io.File("config/fichapp.env");
        if (!envFile.exists()) {
            envFile = new java.io.File("../config/fichapp.env");
        }

        if (envFile.exists()) {
            System.out.println("Cargando configuración desde: " + envFile.getAbsolutePath());
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(envFile.toPath(),
                        java.nio.charset.StandardCharsets.UTF_8);
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#"))
                        continue;

                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();

                            if (key.equals("SPRING_PROFILES_ACTIVE")) {
                                if (System.getProperty("spring.profiles.active") == null) {
                                    System.setProperty("spring.profiles.active", value);
                                    System.out.println("Perfil activo (desde .env): " + value);
                                } else {
                                    System.out.println("Ignorando perfil de .env porque ya hay uno definido: " + System.getProperty("spring.profiles.active"));
                                }
                            } else {
                                System.setProperty(key, value);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al procesar fichapp.env: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró config/fichapp.env. Usando configuración por defecto (local).");
        }

        try {
            SpringApplication.run(FichAppApiApplication.class, args);
        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la aplicación:");
            e.printStackTrace();
            throw e;
        }
    }
}
