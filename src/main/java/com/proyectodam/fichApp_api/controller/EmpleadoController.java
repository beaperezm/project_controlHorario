package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.dto.EmpleadoDetalleDTO;
import com.proyectodam.fichApp_api.dto.EmpleadoEstadoContadorDTO;
import com.proyectodam.fichApp_api.model.Contrato;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.repository.ContratoRepository;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import com.proyectodam.fichApp_api.service.IEmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import com.proyectodam.fichApp_api.dto.EmpleadoDTO;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private IEmpleadoService iEmpleadoService;

    /**
     * Crea un nuevo empleado de forma rápida con la información básica.
     */
    @PostMapping("/alta-rapida")
    public ResponseEntity<Empleado> altaRapidaEmpleado(@RequestBody AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
        Empleado empleado = iEmpleadoService.altaRapidaEmpleado(altaRapidaEmpleadoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(empleado);
    }

    /**
     * Actualiza la información de un empleado existente mediante el proceso de alta
     * rápida.
     */
    @PutMapping("/empleado/{id}")
    public ResponseEntity<Empleado> actualizarEmpleadoEnAltaRapidaEmpleado(@PathVariable int id,
            @RequestBody AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
        Empleado empleado = iEmpleadoService.actualizarEmpleado(id, altaRapidaEmpleadoDTO);
        System.out.println("ESTADO EMPLEADO: " + empleado.getEstado());
        return ResponseEntity.ok(empleado);
    }

    /**
     * Elimina un empleado del sistema.
     */
    @DeleteMapping("/empleado/{id}")
    public ResponseEntity<Void> borrarEmpleadoEnAltaRapidaEmpleado(@PathVariable int id) {
        iEmpleadoService.borrarEmpleadoEnAltaRapidaEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los empleados para autocompletados e interfaz.
     */
    @GetMapping("/all")
    public ResponseEntity<List<EmpleadoDTO>> listarTodos() {
        return ResponseEntity.ok(iEmpleadoService.listarTodos());
    }

    @PostMapping("/empleado/{id}/baja")
    public ResponseEntity<?> borradoLogicoEmpleado(@PathVariable int id) {
        iEmpleadoService.borradoLogicoEmpleado(id);
        return ResponseEntity.ok("Empleado dado de baja correctamente");
    }

    @GetMapping("/all/activos")
    public List<EmpleadoDTO> getAllEmpleadosWithoutInactive() {

        List<Contrato> contratoList = contratoRepository.findContratosConEmpleadoActivo();
        List<EmpleadoDTO> empleadoDTOList = new ArrayList<>();

        for (Contrato contrato : contratoList) {

            Empleado empleado = contrato.getEmpleado();
            EmpleadoDTO empleadoDTO = new EmpleadoDTO();

            empleadoDTO.setIdEmpleado(empleado.getIdEmpleado());
            empleadoDTO.setNombre(empleado.getNombre());
            empleadoDTO.setApellidos(empleado.getApellidos());
            empleadoDTO.setEmail(empleado.getEmail());
            empleadoDTO.setDireccion(empleado.getDireccion());
            empleadoDTO.setTelefono(empleado.getTelefono());
            empleadoDTO.setDni(empleado.getDniNie());
            empleadoDTO.setFechaAltaSistema(empleado.getFechaAltaSistema());
            empleadoDTO.setFechaNacimiento(empleado.getFechaNacimiento());
            empleadoDTO.setEstado(empleado.getEstado().name());
            empleadoDTO.setDepartamento(contrato.getDepartamento().getNombre());
            empleadoDTO.setRol(contrato.getRol().getNombre());

            empleadoDTOList.add(empleadoDTO);
        }

        return empleadoDTOList;
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<EmpleadoDetalleDTO> getEmpleadoDetalle(@PathVariable int id) {
        return ResponseEntity.ok(iEmpleadoService.getEmpleadoDetalle(id));
    }

    @GetMapping("/total_segun_estado")
    public ResponseEntity<EmpleadoEstadoContadorDTO> getTotalEmpleadosEstado() {
        EmpleadoEstadoContadorDTO empleadoEstadoContadorDTO = iEmpleadoService.countEstadoEmpleados();
        return ResponseEntity.ok(empleadoEstadoContadorDTO);
    }
}
