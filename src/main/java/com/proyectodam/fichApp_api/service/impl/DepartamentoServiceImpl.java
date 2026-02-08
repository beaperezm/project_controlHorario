package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.model.Departamento;
import com.proyectodam.fichApp_api.repository.DepartamentoRepository;
import com.proyectodam.fichApp_api.service.IDepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartamentoServiceImpl implements IDepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Override
    public List<Departamento> getAllDepartamentos() {
        return departamentoRepository.findAll();
    }
}
