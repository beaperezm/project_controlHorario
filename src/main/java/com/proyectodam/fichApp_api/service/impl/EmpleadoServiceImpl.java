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
import java.util.stream.Collectors;
import com.proyectodam.fichApp_api.dto.EmpleadoDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public Empleado altaRapidaEmpleado(AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {

                // Validaciones de unicidad antes de crear
                if (altaRapidaEmpleadoDTO.getEmail() != null && !altaRapidaEmpleadoDTO.getEmail().isEmpty()) {
                        empleadoRepository.findByEmail(altaRapidaEmpleadoDTO.getEmail()).ifPresent(e -> {
                                throw new com.proyectodam.fichApp_api.exception.DuplicateFieldException(
                                        "email", altaRapidaEmpleadoDTO.getEmail());
                        });
                }
                if (altaRapidaEmpleadoDTO.getDni() != null && !altaRapidaEmpleadoDTO.getDni().isEmpty()) {
                        empleadoRepository.findByDniNie(altaRapidaEmpleadoDTO.getDni()).ifPresent(e -> {
                                throw new com.proyectodam.fichApp_api.exception.DuplicateFieldException(
                                        "DNI/NIE", altaRapidaEmpleadoDTO.getDni());
                        });
                }

                Empresa empresa = empresaRepository.findById(altaRapidaEmpleadoDTO.getIdEmpresa())
                                .orElseThrow(() -> new RuntimeException("Error: La empresa con ID " + altaRapidaEmpleadoDTO.getIdEmpresa() + " no existe en la base de datos actual."));

                Departamento departamento = departamentoRepository.findById(altaRapidaEmpleadoDTO.getIdDepartamento())
                                .orElseThrow(() -> new RuntimeException("Error: El departamento con ID " + altaRapidaEmpleadoDTO.getIdDepartamento() + " no existe."));

                Rol rol = rolRepository.findById(altaRapidaEmpleadoDTO.getIdRol())
                                .orElseThrow(() -> new RuntimeException("Error: El rol con ID " + altaRapidaEmpleadoDTO.getIdRol() + " no existe."));

                // Se crea el empleado
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

                // La contraseña por defecto es el DNI
                if (altaRapidaEmpleadoDTO.getDni() != null && !altaRapidaEmpleadoDTO.getDni().isEmpty()) {
                        empleado.setPasswordHash(passwordEncoder.encode(altaRapidaEmpleadoDTO.getDni()));
                }
                
                // IMPORTANTE: Asignar PIN por defecto para evitar error 500 (NOT NULL constraint)
                empleado.setPinQuioscoHash(passwordEncoder.encode("0000"));

                try {
                    empleadoRepository.save(empleado);
                } catch (Exception e) {
                    throw new RuntimeException("Error al guardar el empleado: " + e.getMessage());
                }

                // Se crea el contrato
                Contrato contrato = new Contrato();
                contrato.setEmpleado(empleado);
                contrato.setDepartamento(departamento);
                contrato.setRol(rol);
                contrato.setFechaInicio(altaRapidaEmpleadoDTO.getFechaAlta());
                contrato.setTipoContrato("INDEFINIDO");
                contrato.setCreatedAt(LocalDateTime.now());

                // Asignar Horario por defecto
                Horario horarioDefecto = horarioRepository.findAll().stream()
                                .filter(h -> h.getEmpresa().getIdEmpresa() == empresa.getIdEmpresa())
                                .findFirst()
                                .orElse(null);

                if (horarioDefecto != null) {
                        contrato.setHorario(horarioDefecto);
                }

                try {
                    contratoRepository.save(contrato);
                } catch (Exception e) {
                    throw new RuntimeException("Error al crear el contrato del empleado: " + e.getMessage());
                }

                return empleado;
        }

        @Override
        public Empleado actualizarEmpleado(int id, AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {

                Empleado empleado = empleadoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                // Validar unicidad de email (excluyendo al empleado que se está editando)
                if (altaRapidaEmpleadoDTO.getEmail() != null && !altaRapidaEmpleadoDTO.getEmail().isEmpty()) {
                        empleadoRepository.findByEmail(altaRapidaEmpleadoDTO.getEmail()).ifPresent(existente -> {
                                if (existente.getIdEmpleado() != id) {
                                        throw new com.proyectodam.fichApp_api.exception.DuplicateFieldException(
                                                "email", altaRapidaEmpleadoDTO.getEmail());
                                }
                        });
                }
                // Validar unicidad de DNI/NIE (excluyendo al empleado que se está editando)
                if (altaRapidaEmpleadoDTO.getDni() != null && !altaRapidaEmpleadoDTO.getDni().isEmpty()) {
                        empleadoRepository.findByDniNie(altaRapidaEmpleadoDTO.getDni()).ifPresent(existente -> {
                                if (existente.getIdEmpleado() != id) {
                                        throw new com.proyectodam.fichApp_api.exception.DuplicateFieldException(
                                                "DNI/NIE", altaRapidaEmpleadoDTO.getDni());
                                }
                        });
                }

                if (altaRapidaEmpleadoDTO.getNombre() != null) {
                        empleado.setNombre(altaRapidaEmpleadoDTO.getNombre());
                }
                if (altaRapidaEmpleadoDTO.getApellidos() != null) {
                        empleado.setApellidos(altaRapidaEmpleadoDTO.getApellidos());
                }
                if (altaRapidaEmpleadoDTO.getEmail() != null) {
                        empleado.setEmail(altaRapidaEmpleadoDTO.getEmail());
                }
                if (altaRapidaEmpleadoDTO.getDireccion() != null) {
                        empleado.setDireccion(altaRapidaEmpleadoDTO.getDireccion());
                }
                if (altaRapidaEmpleadoDTO.getTelefono() != null) {
                        empleado.setTelefono(altaRapidaEmpleadoDTO.getTelefono());
                }
                if (altaRapidaEmpleadoDTO.getDni() != null) {
                        empleado.setDniNie(altaRapidaEmpleadoDTO.getDni());
                }
                if (altaRapidaEmpleadoDTO.getFechaAlta() != null) {
                        empleado.setFechaAltaSistema(altaRapidaEmpleadoDTO.getFechaAlta());
                }
                if (altaRapidaEmpleadoDTO.getFechaNacimiento() != null) {
                        empleado.setFechaNacimiento(altaRapidaEmpleadoDTO.getFechaNacimiento());
                }

                if (altaRapidaEmpleadoDTO.getEstado() != null) {
                        empleado.setEstado(EstadoEmpleado.valueOf(altaRapidaEmpleadoDTO.getEstado()));
                }
                empleado.setUpdatedAt(LocalDateTime.now());

                empleadoRepository.save(empleado);

                Contrato contrato = contratoRepository.findTopByEmpleadoOrderByFechaInicioDesc(empleado);
                if (contrato != null) {
                        Departamento departamento = departamentoRepository
                                        .findById(altaRapidaEmpleadoDTO.getIdDepartamento())
                                        .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
                        Rol rol = rolRepository.findById(altaRapidaEmpleadoDTO.getIdRol())
                                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                        contrato.setDepartamento(departamento);
                        contrato.setRol(rol);

                        contratoRepository.save(contrato);
                }

                return empleado;
        }

        @Transactional
        @Override
        public void borrarEmpleadoEnAltaRapidaEmpleado(int id) {
                Empleado empleado = empleadoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
                contratoRepository.borrarEmpleadoPorId(id);
                empleadoRepository.delete(empleado);
        }

        @Override
        public List<Empleado> getAllEmpleados() {
                return empleadoRepository.findAll();
        }

        @Override
        public List<EmpleadoDTO> listarTodos() {
                return empleadoRepository.findAll().stream().map(
                                e -> {
                                        EmpleadoDTO dto = new EmpleadoDTO();
                                        dto.setIdEmpleado(e.getIdEmpleado());
                                        dto.setNombre(e.getNombre());
                                        dto.setApellidos(e.getApellidos());
                                        dto.setEmail(e.getEmail());
                                        dto.setTelefono(e.getTelefono());
                                        dto.setEstado(e.getEstado().name());
                                        dto.setPinQuioscoHash(e.getPinQuioscoHash());
                                        return dto;
                                }).collect(Collectors.toList());
        }

        @Override
        public void borradoLogicoEmpleado(int id) {
                Empleado empleado = empleadoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                if (empleado.getEstado() != EstadoEmpleado.INACTIVO) {
                        empleado.setEstado(EstadoEmpleado.INACTIVO);
                        empleadoRepository.save(empleado);
                }
        }

        @Override
        public List<Empleado> getAllEmpleadosWithoutInactive() {
                return empleadoRepository.findByEstadoNot(EstadoEmpleado.INACTIVO);
        }

        @Override
        public EmpleadoDetalleDTO getEmpleadoDetalle(int id) {
                Empleado empleado = empleadoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                BolsaVacaciones bv = bolsaVacacionesRepository.findByEmpleado_IdEmpleadoAndAnio(id,
                                LocalDate.now().getYear());
                int vacT = (bv != null) ? (bv.getDiasTotalesAsignados() != null ? bv.getDiasTotalesAsignados() : 0) : 0;
                int vacD = 0; // Se asume que vendrá de otra parte, por simplificar de momento

                BolsaHoras bh = bolsaHorasRepository.findByEmpleado_IdEmpleado(id);
                int horP = (bh != null && bh.getSaldoHoras() != null && bh.getSaldoHoras() > 0)
                                ? bh.getSaldoHoras().intValue()
                                : 0;
                int horN = (bh != null && bh.getSaldoHoras() != null && bh.getSaldoHoras() < 0)
                                ? bh.getSaldoHoras().intValue()
                                : 0;

                EmpleadoDetalleDTO dto = new EmpleadoDetalleDTO();
                dto.setDiasVacacionesTotales(vacT);
                dto.setDiasVacacionesPendientes(vacD);
                dto.setHorasExtra(horP > 0 ? horP : horN); // Simple conversión de ejemplo

                return dto;
        }

        @Override
        public EmpleadoEstadoContadorDTO countEstadoEmpleados() {
                EmpleadoEstadoContadorDTO estadoDTO = new EmpleadoEstadoContadorDTO();
                estadoDTO.setActivos((long) empleadoRepository.countByEstado(EstadoEmpleado.ACTIVO));
                estadoDTO.setBajaMedica((long) empleadoRepository.countByEstado(EstadoEmpleado.BAJA_MEDICA));

                // Estos podrían necesitar más lógica pero los asigno al count correspondiente o
                // cero si no cuadran
                estadoDTO.setExcedencia(0L);
                estadoDTO.setInactivos((long) empleadoRepository.countByEstado(EstadoEmpleado.INACTIVO));

                // EmpleadoEstadoContadorDTO no tiene setVacaciones ni setIncapacidad
                return estadoDTO;
        }

        @Override
        public long countEmpleadosByEstado(EstadoEmpleado estadoEmpleado) {
                return empleadoRepository.countByEstado(estadoEmpleado);
        }

        @Override
        public void cambiarPassword(int idEmpleado, String nuevaPassword) {
                Empleado empleado = empleadoRepository.findById(idEmpleado)
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
                empleado.setPasswordHash(passwordEncoder.encode(nuevaPassword));
                empleado.setUpdatedAt(LocalDateTime.now());
                empleadoRepository.save(empleado);
        }

}
