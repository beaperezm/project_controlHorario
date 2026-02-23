package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.enums.ModoConexion;
import com.proyectodam.fichApp_api.model.ConfiguracionConexion;
import com.proyectodam.fichApp_api.repository.ConfiguracionConexionRepository;
import com.proyectodam.fichApp_api.service.IConfiguracionConexionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionConexionServiceImpl implements IConfiguracionConexionService {

    @Autowired
    private ConfiguracionConexionRepository repository;

    @Value("${spring.profiles.active:local}")
    private String perfilActivo;

    @PostConstruct
    public void init() {
        // Aseguramos que exista una configuración inicial al arrancar
        inicializarConfiguracion();
    }

    @Override
    @Transactional
    public void inicializarConfiguracion() {
        // Si no existe configuración, creamos una por defecto basada en el perfil activo
        repository.obtenerConfiguracion()
                .orElseGet(() -> {
                    ConfiguracionConexion nueva = new ConfiguracionConexion();
                    nueva.setId(1L);
                    nueva.setModo(ModoConexion.fromPerfil(perfilActivo));
                    return repository.save(nueva);
                });
    }

    @Override
    public ConfiguracionConexion obtenerConfiguracion() {
        // Recuperamos la configuración o creamos una por defecto si fallase la inicialización
        return repository.obtenerConfiguracion()
                .orElseGet(() -> {
                    ConfiguracionConexion nueva = new ConfiguracionConexion();
                    nueva.setId(1L);
                    nueva.setModo(ModoConexion.LOCAL);
                    return repository.save(nueva);
                });
    }

    @Override
    @Transactional
    public ConfiguracionConexion actualizarModo(ModoConexion nuevoModo, String urlPersonalizada) {
        ConfiguracionConexion config = obtenerConfiguracion();

        // Verificamos si realmente hay un cambio de modo para marcar el reinicio
        boolean cambiaModo = !config.getModo().equals(nuevoModo);

        config.setModo(nuevoModo);
        config.setUrlPersonalizada(urlPersonalizada);
        config.setRequiereReinicio(cambiaModo);

        return repository.save(config);
    }

    @Override
    public ModoConexion obtenerModoActual() {
        return obtenerConfiguracion().getModo();
    }

    @Override
    public String obtenerUrlBase() {
        ConfiguracionConexion config = obtenerConfiguracion();

        // Si hay una URL personalizada válida, tiene prioridad
        if (config.getUrlPersonalizada() != null && !config.getUrlPersonalizada().isEmpty()) {
            return config.getUrlPersonalizada();
        }

        // Si no, devolvemos la URL por defecto asociada al modo actual
        return config.getModo().getUrlDefecto();
    }
}
