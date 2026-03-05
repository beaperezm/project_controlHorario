package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.model.Rol;
import com.proyectodam.fichApp_api.service.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private IRolService iRolService;

    @GetMapping("/all")
    public List<Rol> getAllRoles() {
        return iRolService.getAllRoles();
    }
}
