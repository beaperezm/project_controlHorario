package com.proyectodam.fichApp_api.model;

import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.enums.TipoGenero;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "empleados")
@Data
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_empleado", updatable = false, nullable = false)
    private UUID idEmpleado;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true)
    private String email;

    private String passwordHash;
    private String pinQuioscoHash;
    
    @Column(name = "dni_nie")
    private String dniNie;

    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private TipoGenero genero;

    @Enumerated(EnumType.STRING)
    private EstadoEmpleado estado = EstadoEmpleado.ACTIVO;

    private LocalDate fechaAltaSistema = LocalDate.now();
    private LocalDateTime updatedAt;
}
