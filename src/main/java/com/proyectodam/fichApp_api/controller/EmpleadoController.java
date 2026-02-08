package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.service.IEmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private IEmpleadoService iEmpleadoService;

    @PostMapping("/alta-rapida")
    public Empleado altaRapidaEmpleado(@RequestBody AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
        return iEmpleadoService.altaRapidaEmpleado(altaRapidaEmpleadoDTO);
    }

}
