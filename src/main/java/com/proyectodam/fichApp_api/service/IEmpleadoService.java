package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.model.Empleado;

import java.util.List;

public interface IEmpleadoService {

    Empleado altaRapidaEmpleado(AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO);
    Empleado actualizarEmpleadoEnAltaRapidaEmpleado(int id, AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO);
    void borrarEmpleadoEnAltaRapidaEmpleado(int id);

    List<Empleado> getAllEmpleados();

    void borradoLogicoEmpleado(int id);

    List<Empleado> getAllEmpleadosWithoutInactive();
}
