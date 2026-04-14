package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.dto.ContadorDTO;
import com.proyectodam.fichApp_api.dto.FichajeRequestDTO;
import com.proyectodam.fichApp_api.dto.HorasExtraDTO;
import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.enums.EstadoJornada;
import com.proyectodam.fichApp_api.enums.TipoEventoFichaje;
import com.proyectodam.fichApp_api.model.BolsaHoras;
import com.proyectodam.fichApp_api.model.Contrato;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.model.Fichaje;
import com.proyectodam.fichApp_api.repository.BolsaHorasRepository;
import com.proyectodam.fichApp_api.repository.ContratoRepository;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import com.proyectodam.fichApp_api.repository.FichajeRepository;
import com.proyectodam.fichApp_api.service.IFichajeService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FichajeServiceImpl implements IFichajeService {

    private final FichajeRepository fichajeRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ContratoRepository contratoRepository;
    private final BolsaHorasRepository bolsaHorasRepository;


    public FichajeServiceImpl(FichajeRepository fichajeRepository, EmpleadoRepository empleadoRepository, ContratoRepository contratoRepository, BolsaHorasRepository bolsaHorasRepository) {
        this.fichajeRepository = fichajeRepository;
        this.empleadoRepository = empleadoRepository;
        this.contratoRepository = contratoRepository;
        this.bolsaHorasRepository = bolsaHorasRepository;
    }

    public Fichaje registrarFichaje(FichajeRequestDTO fichajeRequestDTO) {

        Empleado empleado = empleadoRepository.findById(fichajeRequestDTO.getIdEmpleado()).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (!empleado.getEstado().equals(EstadoEmpleado.ACTIVO)) {
            throw new RuntimeException("El empleado no está activo");
        }

        EstadoJornada estadoJornada = calcularEstadoActualEmpleado(empleado.getIdEmpleado());

        validarTransicion(estadoJornada, fichajeRequestDTO.getTipoEventoFichaje());

        validarHorario(empleado);


        Fichaje fichaje = new Fichaje();
        fichaje.setEmpleado(empleado);
        fichaje.setTipoEvento(fichajeRequestDTO.getTipoEventoFichaje());
        fichaje.setMetodoRegistro(fichajeRequestDTO.getMetodoFichaje());
        fichaje.setTimestampDispositivo(fichajeRequestDTO.getTimeStampDispositivo());
        fichaje.setTimestampServidor(LocalDateTime.now());
        fichaje.setDispositivoId(fichajeRequestDTO.getDispositivoId());
        fichaje.setComentario(fichajeRequestDTO.getComentario());
        fichaje.setEsValido(true);
        fichaje.setEsModificado(false);
        fichaje.setSincronizado(true);

        if(fichajeRequestDTO.getTipoEventoFichaje() == TipoEventoFichaje.SALIDA) {
            recalcularBolsaHoras(empleado.getIdEmpleado());
        }

        return fichajeRepository.save(fichaje);
    }

    private EstadoJornada calcularEstadoActualEmpleado(int idEmpleado) {

        LocalDate hoy = LocalDate.now();

        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX);

        Optional<Fichaje> ultimoFichaje = fichajeRepository.findTopByEmpleadoIdEmpleadoAndTimestampServidorBetweenOrderByTimestampServidorDesc(idEmpleado, inicioDia, finDia);

        if (ultimoFichaje.isEmpty()) {
            return EstadoJornada.FUERA;
        }

        TipoEventoFichaje ultimoEventoFichaje = ultimoFichaje.get().getTipoEvento();

        return switch (ultimoEventoFichaje) {
            case ENTRADA -> EstadoJornada.DENTRO;
            case PAUSA_INICIO -> EstadoJornada.EN_PAUSA;
            case PAUSA_FIN -> EstadoJornada.DENTRO;
            case SALIDA -> EstadoJornada.FUERA;
        };
    }

    private void validarTransicion(EstadoJornada estadoJornada, TipoEventoFichaje tipoEventoFichaje) {
        boolean valido = switch (estadoJornada) {
            case FUERA -> tipoEventoFichaje == TipoEventoFichaje.ENTRADA;
            case DENTRO ->
                    tipoEventoFichaje == TipoEventoFichaje.PAUSA_INICIO || tipoEventoFichaje == TipoEventoFichaje.SALIDA;
            case EN_PAUSA -> tipoEventoFichaje == TipoEventoFichaje.PAUSA_FIN;
        };

        if (!valido) {
            throw new RuntimeException("Transición de fichaje inválida. Estado actual " + estadoJornada + " Evento recibido: " + tipoEventoFichaje);
        }

    }

    private void validarHorario(Empleado empleado) {
        LocalDate hoy = LocalDate.now();

        Contrato contrato = contratoRepository.buscarContratoActivo(empleado.getIdEmpleado(), hoy).orElseThrow(() -> new RuntimeException("Empleado sin contrato activo"));

        if (contrato.getHorario() == null) {
            throw new RuntimeException("Empleado sin horario asignado");
        }
    }

    public EstadoJornada obtenerEstadoActualEmpleado(int idEmpleado) {
        return calcularEstadoActualEmpleado(idEmpleado);
    }

    @Override
    public List<Fichaje> obtenerFichajesHoy(int idEmpleado) {
        LocalDate hoy = LocalDate.now();

        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(LocalTime.MAX);
        return fichajeRepository.findByEmpleadoIdEmpleadoAndTimestampServidorBetween(idEmpleado, inicio, fin);
    }

    @Override
    public List<Fichaje> obtenerHistorico(int idEmpleado) {
        return fichajeRepository.findByEmpleadoIdEmpleado(idEmpleado);
    }

    @Override
    public ContadorDTO obtenerContador(int idEmpleado) {
        EstadoJornada estadoJornada = obtenerEstadoActualEmpleado(idEmpleado);

        LocalDateTime inicio = null;
        LocalDateTime fin = LocalDateTime.now();

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX);

        List<Fichaje> fichajesHoy = fichajeRepository.findByEmpleadoIdEmpleadoAndTimestampServidorBetween(idEmpleado, inicioDia, finDia).stream().sorted(Comparator.comparing(Fichaje::getTimestampServidor)).toList();

        long segundos = 0;

        LocalDateTime ultimaEntrada = null;

        for (Fichaje f : fichajesHoy) {
            switch (f.getTipoEvento()) {
                case ENTRADA, PAUSA_FIN:
                    if(ultimaEntrada == null) {
                        ultimaEntrada = f.getTimestampServidor();
                    }
                    break;

                case SALIDA, PAUSA_INICIO:
                    if (ultimaEntrada != null && f.getTimestampServidor().isAfter(ultimaEntrada)) {
                        segundos += java.time.Duration.between(ultimaEntrada, f.getTimestampServidor()).getSeconds();
                        ultimaEntrada = null;
                    }
                    break;

            }
        }

        if (estadoJornada == EstadoJornada.DENTRO && ultimaEntrada != null) {
            if(fin.isAfter(ultimaEntrada)) {
                segundos += java.time.Duration.between(ultimaEntrada, fin).getSeconds();
            }

        }
        return new ContadorDTO(idEmpleado, Math.max(segundos, 0));
    }

    @Override
    public HorasExtraDTO calcularHorasExtra(int idEmpleado) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(LocalTime.MAX);

        List<Fichaje> fichajes = fichajeRepository.findByEmpleadoIdEmpleadoAndTimestampServidorBetween(idEmpleado, inicio, fin).stream()
                .sorted(Comparator.comparing(Fichaje::getTimestampServidor))
                .toList();

        if(fichajes.isEmpty()) {
            return new HorasExtraDTO(0, 0, 0, obtenerSaldoActual(idEmpleado));

        }
        long segundosTrabajados = calcularSegundosTrabajados(fichajes, true);
        double horasTrabajadas = segundosTrabajados / 3600.0;

        Contrato contrato = contratoRepository.buscarContratoActivo(idEmpleado, hoy).orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        double horasContrato = contrato.getHorasDiarias() != null ? contrato.getHorasDiarias() : 8.0;

        double horasExtra = horasTrabajadas - horasContrato;

        return new HorasExtraDTO(horasTrabajadas, horasContrato, horasExtra, obtenerSaldoActual(idEmpleado));
    }

    private double obtenerSaldoActual(int idEmpleado) {
        int anio = LocalDate.now().getYear();
        return bolsaHorasRepository.findByEmpleadoIdEmpleadoAndAnio(idEmpleado, anio).map(bh -> bh.getSaldoHoras() != null ? bh.getSaldoHoras() : 0.0).orElse(0.0);
    }

    private long calcularSegundosTrabajados(List<Fichaje> fichajes, boolean incluirTiempoActual) {
        long segundos = 0;
        LocalDateTime inicio = null;

        for (Fichaje fichaje : fichajes) {
            switch (fichaje.getTipoEvento()) {
                case ENTRADA:
                case PAUSA_FIN:
                    if (inicio == null) {
                        inicio = fichaje.getTimestampServidor();
                    }

                    break;
                case PAUSA_INICIO:
                case SALIDA:
                    if (inicio != null) {
                        segundos += Duration.between(inicio, fichaje.getTimestampServidor()).getSeconds();
                        inicio = null;
                    }
                    break;
            }
        }
        if (inicio != null && incluirTiempoActual) {
            segundos += Duration.between(inicio, LocalDateTime.now()).getSeconds();
        }
        return segundos;
    }

    @Override
    public Contrato obtenerContratoActivo(int idEmpleado, LocalDate fecha) {
        return contratoRepository.buscarContratoActivo(idEmpleado, fecha).orElseThrow(() -> new RuntimeException("Empleado sin contrato activo"));
    }

    public double recalcularBolsaHoras(int idEmpleado) {

        LocalDate hoy = LocalDate.now();
        int anio = hoy.getYear();


        LocalDate inicioAnio = LocalDate.of(anio, 1, 1);

        List<Fichaje> fichajes = fichajeRepository.findByEmpleadoIdEmpleadoAndTimestampServidorBetween(idEmpleado, inicioAnio.atStartOfDay(), hoy.atTime(LocalTime.MAX)).stream()
                .sorted(Comparator.comparing(Fichaje::getTimestampServidor))
                .toList();

        if(fichajes.isEmpty()) {
            return 0.0;
        }

        Map<LocalDate, List<Fichaje>> fichajePorDia = fichajes.stream()
                .collect(Collectors.groupingBy(f -> f.getTimestampServidor().toLocalDate(), TreeMap::new, Collectors.toList()));

        double saldo = 0.0;

        for (Map.Entry<LocalDate, List<Fichaje>> entry : fichajePorDia.entrySet()) {
            LocalDate fecha = entry.getKey();

            List<Fichaje> fichajesDia = entry.getValue().stream().sorted(Comparator.comparing(Fichaje::getTimestampServidor)).toList();

            if(fichajesDia.isEmpty()) continue;

            boolean tieneEntrada = fichajesDia.stream().anyMatch(f -> f.getTipoEvento() == TipoEventoFichaje.ENTRADA);

            if(!tieneEntrada) continue;

            long segundos = calcularSegundosTrabajados(fichajesDia, false);
            double horasTrabajadas = segundos / 3600.0;

            Contrato contrato = contratoRepository.buscarContratoActivo(idEmpleado, fecha).orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

            double horasContrato = contrato.getHorasDiarias() != null ? contrato.getHorasDiarias() : 8.0;

            boolean jornadaTerminada = fichajesDia.stream().anyMatch(f -> f.getTipoEvento() == TipoEventoFichaje.SALIDA);

            double horasExtra = 0.0;

            if(jornadaTerminada) {
                horasExtra = horasTrabajadas - horasContrato;
            }

            saldo += horasExtra;
        }

        Empleado empleado = empleadoRepository.findById(idEmpleado).orElseThrow();

        BolsaHoras bolsaHoras = bolsaHorasRepository.findByEmpleadoIdEmpleadoAndAnio(idEmpleado, anio).orElseGet(() -> {
            BolsaHoras nueva = new BolsaHoras();
            nueva.setEmpleado(empleado);
            nueva.setAnio(anio);
            return nueva;
        });

        bolsaHoras.setSaldoHoras(saldo);
        bolsaHoras.setUltimaActualizacion(hoy);

        bolsaHorasRepository.save(bolsaHoras);

        return saldo;

    }

}