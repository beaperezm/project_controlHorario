package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.ContadorDTO;
import com.proyectodam.fichApp_api.dto.FichajeRequestDTO;
import com.proyectodam.fichApp_api.dto.HorasExtraDTO;
import com.proyectodam.fichApp_api.enums.EstadoJornada;
import com.proyectodam.fichApp_api.model.Contrato;
import com.proyectodam.fichApp_api.model.Fichaje;

import java.time.LocalDate;
import java.util.List;

public interface IFichajeService {

    Fichaje registrarFichaje(FichajeRequestDTO fichajeRequestDTO);
    EstadoJornada obtenerEstadoActualEmpleado(int idEmpleado);
    List<Fichaje> obtenerFichajesHoy(int idEmpleado);
    List<Fichaje> obtenerHistorico(int idEmpleado);
    ContadorDTO obtenerContador(int idEmpleado);
    HorasExtraDTO calcularHorasExtra(int idEmpleado);
    Contrato obtenerContratoActivo(int idEmpleado, LocalDate fecha);
    double recalcularBolsaHoras(int idEmpleado);

}