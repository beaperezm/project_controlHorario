package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.model.Rol;
import com.proyectodam.fichApp_api.repository.RolRepository;
import com.proyectodam.fichApp_api.service.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements IRolService {

    @Autowired
    private RolRepository rolRepository;
    @Override
    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }
}
