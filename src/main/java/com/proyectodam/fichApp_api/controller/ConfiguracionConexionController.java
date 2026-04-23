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

    @GetMapping("/conexion")
    public ResponseEntity<ConfiguracionConexion> obtenerConfiguracion() {
        return ResponseEntity.ok(configuracionService.obtenerConfiguracion());
    }

    @PutMapping("/conexion")
    public ResponseEntity<Map<String, Object>> actualizarConfiguracion(@RequestBody Map<String, String> request) {
        String modoStr = request.get("modo");
        String urlPersonalizada = request.get("urlPersonalizada");
        String supaUrl = request.get("supaUrl");
        String supaKey = request.get("supaKey");
        String supaDbPass = request.get("supaDbPass");
        String supaDbUser = request.get("supaDbUser");
        String supaDbName = request.get("supaDbName");
        String supaDbHost = request.get("supaDbHost");

        try {
            ModoConexion nuevoModo = ModoConexion.valueOf(modoStr.toUpperCase());
            ConfiguracionConexion configuracion = configuracionService.actualizarModo(nuevoModo, urlPersonalizada, supaUrl, supaKey, supaDbPass, supaDbUser, supaDbName, supaDbHost);

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

    @PostMapping("/inicializar-db")
    public ResponseEntity<Map<String, Object>> inicializarBaseDatos() {
        Map<String, Object> resultado = configuracionService.inicializarBaseDatosSupabase();
        if ((Boolean) resultado.get("success")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.internalServerError().body(resultado);
        }
    }

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
