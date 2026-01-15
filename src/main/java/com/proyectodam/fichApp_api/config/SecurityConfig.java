package com.proyectodam.fichApp_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configuración de la cadena de filtros de seguridad.
     * Aquí definimos quién puede entrar a qué rutas.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitamos CSRF ya que nuestra API es Stateless y usaremos tokens/Basic Auth más adelante
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Configuración de sesiones: Stateless (sin estado) para una API REST
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. Autorización de peticiones
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas de Autenticación (Login, Registro)
                .requestMatchers("/auth/**").permitAll()
                
                // Rutas públicas de Documentación (Swagger/OpenAPI)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // ⚠️ ACCESO TEMPORAL PARA DESARROLLO: 
                // Permitimos que todos los módulos (Fichajes, Vacaciones, Docs) funcionen sin bloqueo
                .anyRequest().permitAll() 
            );

        return http.build();
    }

    /**
     * El Bean PasswordEncoder para el hash de contraseñas y PINs.
     * Se usará en el Service de Empleados y en el AuthController.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}