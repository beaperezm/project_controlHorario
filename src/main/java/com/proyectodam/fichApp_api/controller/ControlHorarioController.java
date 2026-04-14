package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.ContadorDTO;
import com.proyectodam.fichApp_api.dto.FichajeDTO;
import com.proyectodam.fichApp_api.dto.FichajeRequestDTO;
import com.proyectodam.fichApp_api.dto.HorasExtraDTO;
import com.proyectodam.fichApp_api.enums.EstadoJornada;
import com.proyectodam.fichApp_api.model.Fichaje;
import com.proyectodam.fichApp_api.service.IFichajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("empleado/control-horario")
public class ControlHorarioController {
    private final IFichajeService iFichajeService;

    public ControlHorarioController(IFichajeService iFichajeService) {
        this.iFichajeService = iFichajeService;
    }

    @PostMapping("/fichar")
    public ResponseEntity<?> ficharEmpleado(@RequestBody FichajeRequestDTO fichajeRequestDTO) {
        try {
            Fichaje fichaje = iFichajeService.registrarFichaje(fichajeRequestDTO);
            return ResponseEntity.ok(fichaje);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(e.getMessage());
        }

    }

    @GetMapping("/estado/{idEmpleado}")
    public EstadoJornada obtenerEstado(@PathVariable int idEmpleado) {
        return iFichajeService.obtenerEstadoActualEmpleado(idEmpleado);
    }

    @GetMapping("/fichajes-hoy/{idEmpleado}")
    public List<FichajeDTO> obtenerFichajesHoy(@PathVariable int idEmpleado) {
        return iFichajeService.obtenerFichajesHoy(idEmpleado).stream().map(this::convertirADTO).toList();
    }

    @GetMapping("/fichajes-historico/{idEmpleado}")
    public List<FichajeDTO> obtenerHistorico(@PathVariable int idEmpleado) {
        return iFichajeService.obtenerHistorico(idEmpleado).stream().map(this::convertirADTO).toList();
    }

    private FichajeDTO convertirADTO(Fichaje fichaje) {
        return new FichajeDTO(
                fichaje.getIdFichaje(),
                fichaje.getTipoEvento(),
                fichaje.getMetodoRegistro(),
                fichaje.getTimestampServidor()
        );
    }

    @GetMapping("/contador/{idEmpleado}")
    public ContadorDTO obtenerContador(@PathVariable int idEmpleado) {
        return iFichajeService.obtenerContador(idEmpleado);
    }

    @GetMapping("/horas-extra/{idEmpleado}")
    public HorasExtraDTO obtenerHorasExtra(@PathVariable int idEmpleado) {
        return iFichajeService.calcularHorasExtra(idEmpleado);
    }

    @GetMapping("/bolsa-horas/{idEmpleado}")
    public ResponseEntity<Double> obtenerBolsaHoras(@PathVariable int idEmpleado) {
        double saldo = iFichajeService.recalcularBolsaHoras(idEmpleado);
        return ResponseEntity.ok(saldo);
    }
}