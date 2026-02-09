package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.service.IEmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private IEmpleadoService iEmpleadoService;

    @PostMapping("/alta-rapida")
    public ResponseEntity<Empleado> altaRapidaEmpleado(@RequestBody AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
        Empleado empleado = iEmpleadoService.altaRapidaEmpleado(altaRapidaEmpleadoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(empleado);
    }

    @PutMapping("/empleado/{id}")
    public ResponseEntity<Empleado> actualizarEmpleadoEnAltaRapidaEmpleado(@PathVariable int id, @RequestBody AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
        Empleado empleado = iEmpleadoService.actualizarEmpleadoEnAltaRapidaEmpleado(id, altaRapidaEmpleadoDTO);
        return ResponseEntity.ok(empleado);
    }

    @DeleteMapping("/empleado/{id}")
    public ResponseEntity<Void> borrarEmpleadoEnAltaRapidaEmpleado(@PathVariable int id) {
        iEmpleadoService.borrarEmpleadoEnAltaRapidaEmpleado(id);
        return ResponseEntity.noContent().build();
    }

}
