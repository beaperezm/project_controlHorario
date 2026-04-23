package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.model.Recordatorio;
import com.proyectodam.fichApp_api.service.RecordatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recordatorios")
public class RecordatorioController {

    @Autowired
    private RecordatorioService recordatorioService;

    @GetMapping
    public ResponseEntity<List<Recordatorio>> obtenerRecordatoriosMes(
            @RequestParam int year, @RequestParam int month,
            @RequestParam(required = false) Integer idEmpleado) {
        return ResponseEntity.ok(recordatorioService.obtenerRecordatoriosPorMes(year, month, idEmpleado));
    }

    @PostMapping
    public ResponseEntity<Recordatorio> crearRecordatorio(@RequestBody Recordatorio recordatorio) {
        return ResponseEntity.ok(recordatorioService.crearRecordatorio(recordatorio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRecordatorio(@PathVariable Long id) {
        recordatorioService.eliminarRecordatorio(id);
        return ResponseEntity.noContent().build();
    }
}
