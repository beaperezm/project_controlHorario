package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.dto.EmpleadoDetalleDTO;
import com.proyectodam.fichApp_api.dto.EmpleadoEstadoContadorDTO;
import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.model.*;
import com.proyectodam.fichApp_api.repository.*;
import com.proyectodam.fichApp_api.service.IEmpleadoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmpleadoServiceImpl implements IEmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private BolsaVacacionesRepository bolsaVacacionesRepository;

    @Autowired
    private BolsaHorasRepository bolsaHorasRepository;

    @Autowired
    private HorarioRepository horarioRepository;


    @Override
    public Empleado altaRapidaEmpleado(AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {

        Empresa empresa = empresaRepository.findById(altaRapidaEmpleadoDTO.getIdEmpresa()).orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        Departamento departamento = departamentoRepository.findById(altaRapidaEmpleadoDTO.getIdDepartamento()).orElseThrow(() -> new RuntimeException("Departamento no encontrado"));

        Rol rol = rolRepository.findById(altaRapidaEmpleadoDTO.getIdRol()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Horario horario = horarioRepository.findById(altaRapidaEmpleadoDTO.getIdHorario()).orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        //Se crea el empleado
        Empleado empleado = new Empleado();
        empleado.setNombre(altaRapidaEmpleadoDTO.getNombre());
        empleado.setApellidos(altaRapidaEmpleadoDTO.getApellidos());
        empleado.setEmail(altaRapidaEmpleadoDTO.getEmail());
        empleado.setDireccion(altaRapidaEmpleadoDTO.getDireccion());
        empleado.setTelefono(altaRapidaEmpleadoDTO.getTelefono());
        empleado.setDniNie(altaRapidaEmpleadoDTO.getDni());
        empleado.setEstado(EstadoEmpleado.ACTIVO);
        empleado.setFechaAltaSistema(altaRapidaEmpleadoDTO.getFechaAlta());
        empleado.setFechaNacimiento(altaRapidaEmpleadoDTO.getFechaNacimiento());
        empleado.setEmpresa(empresa);
        empleadoRepository.save(empleado);

        //Se crea el contrato
        Contrato contrato = new Contrato();
        contrato.setEmpleado(empleado);
        contrato.setDepartamento(departamento);
        contrato.setRol(rol);
        contrato.setFechaInicio(altaRapidaEmpleadoDTO.getFechaAlta());
        contrato.setCreatedAt(LocalDateTime.now());
        contrato.setHorario(horario);
        contratoRepository.save(contrato);

        return empleado;
    }

    @Override
    public Empleado actualizarEmpleado(int id, AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
      //  Empresa empresa = empresaRepository.findById(altaRapidaEmpleadoDTO.getIdEmpresa()).orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        Empleado empleado = empleadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));


        if(altaRapidaEmpleadoDTO.getNombre() != null) {
            empleado.setNombre(altaRapidaEmpleadoDTO.getNombre());
        }
        if(altaRapidaEmpleadoDTO.getApellidos() != null) {
            empleado.setApellidos(altaRapidaEmpleadoDTO.getApellidos());
        }
        if(altaRapidaEmpleadoDTO.getEmail() != null) {
            empleado.setEmail(altaRapidaEmpleadoDTO.getEmail());
        }
        if(altaRapidaEmpleadoDTO.getDireccion() != null) {
            empleado.setDireccion(altaRapidaEmpleadoDTO.getDireccion());
        }
        if(altaRapidaEmpleadoDTO.getTelefono() != null) {
            empleado.setTelefono(altaRapidaEmpleadoDTO.getTelefono());
        }
        if(altaRapidaEmpleadoDTO.getDni() != null) {
            empleado.setDniNie(altaRapidaEmpleadoDTO.getDni());
        }
        if(altaRapidaEmpleadoDTO.getFechaAlta() != null) {
            empleado.setFechaAltaSistema(altaRapidaEmpleadoDTO.getFechaAlta());
        }
        if(altaRapidaEmpleadoDTO.getFechaNacimiento() != null) {
            empleado.setFechaNacimiento(altaRapidaEmpleadoDTO.getFechaNacimiento());
        }
        empleado.setEstado(EstadoEmpleado.valueOf(altaRapidaEmpleadoDTO.getEstado()));
        empleado.setUpdatedAt(LocalDateTime.now());

        empleadoRepository.save(empleado);



        Contrato contrato = contratoRepository.findTopByEmpleadoOrderByFechaInicioDesc(empleado);
        if(contrato != null) {
            Departamento departamento = departamentoRepository.findById(altaRapidaEmpleadoDTO.getIdDepartamento()).orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
            Rol rol = rolRepository.findById(altaRapidaEmpleadoDTO.getIdRol()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            Horario horario = horarioRepository.findById(altaRapidaEmpleadoDTO.getIdHorario()).orElseThrow(() -> new RuntimeException("Horario no encontrado"));
            contrato.setDepartamento(departamento);
            contrato.setRol(rol);
            contrato.setHorario(horario);

            contratoRepository.save(contrato);
        }

        return empleado;
    }

    @Transactional
    @Override
    public void borrarEmpleadoEnAltaRapidaEmpleado(int id) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        contratoRepository.borrarEmpleadoPorId(id);
        empleadoRepository.delete(empleado);
    }

    @Override
    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    @Transactional
    @Override
    public void borradoLogicoEmpleado(int id) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        empleado.setEstado(EstadoEmpleado.INACTIVO);
        empleado.setUpdatedAt(LocalDateTime.now());

        empleadoRepository.save(empleado);

    }

    public List<Empleado> getAllEmpleadosWithoutInactive() {
        return empleadoRepository.findByEstadoNot(EstadoEmpleado.INACTIVO);
    }

    @Override
    public EmpleadoDetalleDTO getEmpleadoDetalle(int id) {

        Empleado empleado = empleadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        EmpleadoDetalleDTO empleadoDetalleDTO = new EmpleadoDetalleDTO();
        empleadoDetalleDTO.setIdEmpleado(empleado.getIdEmpleado());
        empleadoDetalleDTO.setNombreApellidos(empleado.getNombre() + " " + empleado.getApellidos());
        empleadoDetalleDTO.setEmail(empleado.getEmail());
        empleadoDetalleDTO.setTelefono(empleado.getTelefono());
        empleadoDetalleDTO.setDireccion(empleado.getDireccion());

        Contrato contrato = contratoRepository.findTopByEmpleadoOrderByFechaInicioDesc(empleado);
        if(contrato != null) {
            empleadoDetalleDTO.setDepartamento(contrato.getDepartamento().getNombre());
            empleadoDetalleDTO.setRol(contrato.getRol().getNombre());

            if(contrato.getHorario() != null) {
                empleadoDetalleDTO.setHorario(contrato.getHorario().getNombre());
                empleadoDetalleDTO.setConfiguracionHorario(contrato.getHorario().getConfiguracionSemanal());
            }

            int anioActual = LocalDate.now().getYear();
            BolsaVacaciones bolsaVacaciones = bolsaVacacionesRepository.findByEmpleado_IdEmpleadoAndAnio(id, anioActual);

            if(bolsaVacaciones != null) {
                empleadoDetalleDTO.setDiasVacacionesTotales(bolsaVacaciones.getDiasTotalesAsignados());
                empleadoDetalleDTO.setDiasVacacionesPendientes(bolsaVacaciones.getDiasPendientesAnioAnterior());
            }

            BolsaHoras bolsaHoras = bolsaHorasRepository.findByEmpleado_IdEmpleado(id);

            if(bolsaHoras != null) {
                empleadoDetalleDTO.setHorasExtra(bolsaHoras.getSaldoHoras());
            }
        }
        return empleadoDetalleDTO;
    }

    @Override
    public EmpleadoEstadoContadorDTO countEstadoEmpleados() {

        EmpleadoEstadoContadorDTO empleadoEstadoContadorDTO = new EmpleadoEstadoContadorDTO();
        empleadoEstadoContadorDTO.setTotalEmpleados(empleadoRepository.count());
        empleadoEstadoContadorDTO.setActivos(empleadoRepository.countByEstado(EstadoEmpleado.ACTIVO));
        empleadoEstadoContadorDTO.setInactivos(empleadoRepository.countByEstado(EstadoEmpleado.INACTIVO));
        empleadoEstadoContadorDTO.setBajaMedica(empleadoRepository.countByEstado(EstadoEmpleado.BAJA_MEDICA));
        empleadoEstadoContadorDTO.setExcedencia(empleadoRepository.countByEstado(EstadoEmpleado.EXCEDENCIA));
        return empleadoEstadoContadorDTO;
    }

    @Override
    public long countEmpleadosByEstado(EstadoEmpleado estadoEmpleado) {
        return empleadoRepository.countByEstado(estadoEmpleado);
    }


}
