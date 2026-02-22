package com.proyectodam.fichApp_api.repository;

import com.proyectodam.fichApp_api.model.ConfiguracionConexion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionConexionRepository extends JpaRepository<ConfiguracionConexion, Long> {

    /**
     * Recupera la configuración de conexión activa.
     * Al ser una aplicación monopuesto/cliente único, utilizamos siempre el ID 1.
     */
    default Optional<ConfiguracionConexion> obtenerConfiguracion() {
        return findById(1L);
    }
}
