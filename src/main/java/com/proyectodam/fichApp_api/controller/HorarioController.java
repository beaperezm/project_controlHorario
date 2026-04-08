package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.model.Horario;
import com.proyectodam.fichApp_api.service.IHorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    @Autowired
    private IHorarioService iHorarioService;

    @GetMapping("/all")
    public List<Horario> getAllHorarios() {
        return iHorarioService.getAllHorarios();
    }
}
