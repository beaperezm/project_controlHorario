package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.enums.ModoConexion;
import com.proyectodam.fichApp_api.model.ConfiguracionConexion;

public interface IConfiguracionConexionService {

    ConfiguracionConexion obtenerConfiguracion();

    ConfiguracionConexion actualizarModo(ModoConexion nuevoModo, String urlPersonalizada);

    ModoConexion obtenerModoActual();

    String obtenerUrlBase();

    void inicializarConfiguracion();
}
