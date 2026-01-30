package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.model.SolicitudAusencia;
import com.proyectodam.fichApp_api.service.IVacacionesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacaciones")
public class VacacionesController {

    private final IVacacionesService vacacionesService;

    public VacacionesController(IVacacionesService vacacionesService) {
        this.vacacionesService = vacacionesService;
    }

    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<SolicitudAusencia>> obtenerPorEmpleado(
            @PathVariable Integer idEmpleado) {

        return ResponseEntity.ok(
                vacacionesService.obtenerSolicitudesPorEmpleado(idEmpleado)
        );
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudAusencia> aprobarSolicitud(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                vacacionesService.aprobarSolicitud(id)
        );
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudAusencia> rechazarSolicitud(
            @PathVariable Integer id,
            @RequestParam String comentario) {

        return ResponseEntity.ok(
                vacacionesService.rechazarSolicitud(id, comentario)
        );
    }
}

