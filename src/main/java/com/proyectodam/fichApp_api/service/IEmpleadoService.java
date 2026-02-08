package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.model.Empleado;

public interface IEmpleadoService {

    Empleado altaRapidaEmpleado(AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO);
}
