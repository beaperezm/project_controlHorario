package com.proyectodam.fichApp_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfig {

    @Bean
    public CommandLineRunner dropEstadoFirmaConstraint(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE documentos DROP CONSTRAINT IF EXISTS documentos_estado_firma_check");
                System.out.println("Dropped constraint documentos_estado_firma_check to support new enum values.");
            } catch (Exception e) {
                System.out.println("Could not drop constraint: " + e.getMessage());
            }
        };
    }
}
