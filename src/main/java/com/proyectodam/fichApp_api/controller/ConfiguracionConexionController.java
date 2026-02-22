package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.enums.ModoConexion;
import com.proyectodam.fichApp_api.model.ConfiguracionConexion;
import com.proyectodam.fichApp_api.service.IConfiguracionConexionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionConexionController {

    @Autowired
    private IConfiguracionConexionService configuracionService;

    /**
     * Devuelve la configuración actual de la conexión.
     */
    @GetMapping("/conexion")
    public ResponseEntity<ConfiguracionConexion> obtenerConfiguracion() {
        return ResponseEntity.ok(configuracionService.obtenerConfiguracion());
    }

    /**
     * Actualiza el modo de conexión y la URL personalizada si es necesario.
     * Devuelve la nueva configuración y si requiere reinicio.
     */
    @PutMapping("/conexion")
    public ResponseEntity<Map<String, Object>> actualizarConfiguracion(
            @RequestBody Map<String, String> request) {

        String modoStr = request.get("modo");
        String urlPersonalizada = request.get("urlPersonalizada");

        try {
            ModoConexion nuevoModo = ModoConexion.valueOf(modoStr.toUpperCase());
            ConfiguracionConexion configuracion = configuracionService.actualizarModo(nuevoModo, urlPersonalizada);

            Map<String, Object> response = new HashMap<>();
            response.put("configuracion", configuracion);
            response.put("mensaje", "Configuración actualizada correctamente");

            if (configuracion.isRequiereReinicio()) {
                response.put("advertencia", "Se requiere reiniciar la aplicación para aplicar los cambios");
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "El modo de conexión indicado no es válido: " + modoStr);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Consulta el modo de conexión activo y la URL base que se está utilizando.
     */
    @GetMapping("/modo-actual")
    public ResponseEntity<Map<String, String>> obtenerModoActual() {
        ModoConexion modo = configuracionService.obtenerModoActual();
        String urlBase = configuracionService.obtenerUrlBase();

        Map<String, String> response = new HashMap<>();
        response.put("modo", modo.name());
        response.put("urlBase", urlBase);
        response.put("perfilSpring", modo.getPerfilSpring());

        return ResponseEntity.ok(response);
    }
}
