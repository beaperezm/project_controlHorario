package com.proyectodam.fichApp_api.model;

import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.enums.TipoGenero;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "empleados")
@Data
public class Empleado {

  /*  @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_empleado", updatable = false, nullable = false)
    private UUID idEmpleado2;*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado", updatable = false, nullable = false)
    private int idEmpleado;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "pin_quiosco_hash")
    private String pinQuioscoHash;

    @Column(name = "img_perfil_url")
    private String imgPerfilUrl;

    @Column(name = "dni_nie", length = 20)
    private String dniNie;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private TipoGenero genero;

    @Enumerated(EnumType.STRING)
    private EstadoEmpleado estado = EstadoEmpleado.ACTIVO;

    @Column(name = "fecha_alta_sistema")
    private LocalDate fechaAltaSistema = LocalDate.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    private String direccion;

    @Column(nullable = false)
    private String telefono;


    @Column(length = 20)
    private String nuss;
}
