package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.enums.ModoConexion;
import com.proyectodam.fichApp_api.model.ConfiguracionConexion;
import java.util.Map;

public interface IConfiguracionConexionService {

    ConfiguracionConexion obtenerConfiguracion();
    
    // ConfiguracionConexion actualizarModo(ModoConexion nuevoModo, String urlPersonalizada, String supaUrl, String supaKey);
    ConfiguracionConexion actualizarModo(ModoConexion nuevoModo, String urlPersonalizada, String supaUrl, String supaKey, String supaDbPass, String supaDbUser, String supaDbName, String supaDbHost);
    
    Map<String, Object> inicializarBaseDatosSupabase();

    ModoConexion obtenerModoActual();
    String obtenerUrlBase();
    void inicializarConfiguracion();
}
