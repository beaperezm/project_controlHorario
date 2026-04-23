package com.proyectodam.fichApp_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Configuración de la cadena de filtros de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF (API Stateless)
                .csrf(AbstractHttpConfigurer::disable)
                
                // 2. Habilitar CORS (usando la configuración de CorsConfig)
                .cors(Customizer.withDefaults())

                // 3. Sesiones sin estado
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Autorización de peticiones
                .authorizeHttpRequests(auth -> auth
                        //  ACCESO TEMPORAL PARA DESARROLLO (Todo abierto)
                        // NOTA: Se eliminan matchers específicos de Swagger/Auth por ser redundantes con permitAll()
                        .anyRequest().permitAll());

        return http.build();
    }

    /**
     * Bean PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
