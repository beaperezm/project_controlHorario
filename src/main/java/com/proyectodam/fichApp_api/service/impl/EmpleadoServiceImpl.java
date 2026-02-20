package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.model.*;
import com.proyectodam.fichApp_api.repository.*;
import com.proyectodam.fichApp_api.service.IEmpleadoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Empleado altaRapidaEmpleado(AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {

        Empresa empresa = empresaRepository.findById(altaRapidaEmpleadoDTO.getIdEmpresa()).orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        Departamento departamento = departamentoRepository.findById(altaRapidaEmpleadoDTO.getIdDepartamento()).orElseThrow(() -> new RuntimeException("Departamento no encontrado"));

        Rol rol = rolRepository.findById(altaRapidaEmpleadoDTO.getIdRol()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));


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
        contratoRepository.save(contrato);

        return empleado;
    }

    @Override
    public Empleado actualizarEmpleadoEnAltaRapidaEmpleado(int id, AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
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
        empleado.setEstado(EstadoEmpleado.ACTIVO);
        empleado.setUpdatedAt(LocalDateTime.now());
        return empleadoRepository.save(empleado);
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
}
