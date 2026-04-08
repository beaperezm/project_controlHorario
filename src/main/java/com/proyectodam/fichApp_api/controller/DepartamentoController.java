package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.model.Departamento;
import com.proyectodam.fichApp_api.service.IDepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    @Autowired
    private IDepartamentoService iDepartamentoService;

    @GetMapping("/all")
    public List<Departamento> getAllDepartamentos() {
        return iDepartamentoService.getAllDepartamentos();
    }
}
