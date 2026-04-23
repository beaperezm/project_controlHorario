package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.model.Horario;
import com.proyectodam.fichApp_api.repository.HorarioRepository;
import com.proyectodam.fichApp_api.service.IHorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorarioServiceImpl implements IHorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Override
    public List<Horario> getAllHorarios() {
        return horarioRepository.findAll();
    }
}
