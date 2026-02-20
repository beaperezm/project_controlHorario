package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichApp_api.dto.EmpleadoDTO;
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

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private IEmpleadoService iEmpleadoService;

    @PostMapping("/alta-rapida")
    public ResponseEntity<Empleado> altaRapidaEmpleado(@RequestBody AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
        Empleado empleado = iEmpleadoService.altaRapidaEmpleado(altaRapidaEmpleadoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(empleado);
    }

    @PutMapping("/empleado/{id}")
    public ResponseEntity<Empleado> actualizarEmpleadoEnAltaRapidaEmpleado(@PathVariable int id, @RequestBody AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) {
        Empleado empleado = iEmpleadoService.actualizarEmpleadoEnAltaRapidaEmpleado(id, altaRapidaEmpleadoDTO);
        return ResponseEntity.ok(empleado);
    }

    @DeleteMapping("/empleado/{id}")
    public ResponseEntity<Void> borrarEmpleadoEnAltaRapidaEmpleado(@PathVariable int id) {
        iEmpleadoService.borrarEmpleadoEnAltaRapidaEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public List<Empleado> getAllEmpleados() {
        return iEmpleadoService.getAllEmpleados();
    }

    @PostMapping("empleado/{id}/baja")
    public ResponseEntity<?> borradoLogicoEmpleado(@PathVariable int id) {
        iEmpleadoService.borradoLogicoEmpleado(id);
        return ResponseEntity.ok("Empleado dado de baja correctamente");
    }

    @GetMapping("/all/activos")
    public List<EmpleadoDTO> getAllEmpleadosWithoutInactive() {

        List<Contrato> contratoList = contratoRepository.findContratosConEmpleadoActivo();
        List<EmpleadoDTO> empleadoDTOList = new ArrayList<>();

        for(Contrato contrato : contratoList) {

            Empleado empleado = contrato.getEmpleado();
            EmpleadoDTO empleadoDTO = new EmpleadoDTO();

            empleadoDTO.setIdEmpleado(empleado.getIdEmpleado());
            empleadoDTO.setNombre(empleado.getNombre());
            empleadoDTO.setApellidos(empleado.getApellidos());
            empleadoDTO.setEmail(empleado.getEmail());
            empleadoDTO.setDireccion(empleado.getDireccion());
            empleadoDTO.setTelefono(empleado.getTelefono());
            empleadoDTO.setDni(empleado.getDniNie());
            empleadoDTO.setFechaAlta(empleado.getFechaAltaSistema());
            empleadoDTO.setFechaNacimiento(empleado.getFechaNacimiento());
            empleadoDTO.setEstado(empleado.getEstado().name());
            empleadoDTO.setDepartamento(contrato.getDepartamento().getNombre());
            empleadoDTO.setRol(contrato.getRol().getNombre());


            empleadoDTOList.add(empleadoDTO);
        }

            return empleadoDTOList;
    }

}
