package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.dto.EmpleadoDetalleDTO;
import com.proyectodam.fichApp_api.dto.EmpleadoEstadoContadorDTO;
import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.dto.EmpleadoDTO;

import java.util.List;

public interface IEmpleadoService {

    Empleado altaRapidaEmpleado(AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO);

    Empleado actualizarEmpleado(int id, AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO);

    void borrarEmpleadoEnAltaRapidaEmpleado(int id);

    List<Empleado> getAllEmpleados();

    List<EmpleadoDTO> listarTodos();

    void borradoLogicoEmpleado(int id);

    List<Empleado> getAllEmpleadosWithoutInactive();

    EmpleadoDetalleDTO getEmpleadoDetalle(int id);

    EmpleadoEstadoContadorDTO countEstadoEmpleados();

    long countEmpleadosByEstado(EstadoEmpleado estadoEmpleado);
}
