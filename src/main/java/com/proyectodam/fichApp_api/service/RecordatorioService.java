package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.model.Recordatorio;
import com.proyectodam.fichApp_api.repository.RecordatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecordatorioService {

    @Autowired
    private RecordatorioRepository recordatorioRepository;

    public List<Recordatorio> obtenerRecordatoriosPorMes(int year, int month, Integer idEmpleado) {
        // Obtenemos el primer día del mes y el último
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        if (idEmpleado != null) {
            return recordatorioRepository.findByIdEmpleadoAndFechaBetweenOrderByFechaAsc(idEmpleado, start, end);
        } else {
            return recordatorioRepository.findByFechaBetweenOrderByFechaAsc(start, end);
        }
    }

    public Recordatorio crearRecordatorio(Recordatorio recordatorio) {
        return recordatorioRepository.save(recordatorio);
    }

    public void eliminarRecordatorio(Long id) {
        recordatorioRepository.deleteById(id);
    }
}
