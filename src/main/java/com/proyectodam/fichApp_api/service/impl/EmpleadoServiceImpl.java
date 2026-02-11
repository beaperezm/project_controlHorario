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
        Empresa empresa = empresaRepository.findById(altaRapidaEmpleadoDTO.getIdEmpresa()).orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        Empleado empleado = empleadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

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

        return empleadoRepository.save(empleado);
    }

    @Transactional
    @Override
    public Empleado borrarEmpleadoEnAltaRapidaEmpleado(int id) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        contratoRepository.borrarEmpleadoPorId(id);
        empleadoRepository.delete(empleado);

        return empleado;

    }
}
