package com.proyectodam.fichApp_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {
    private Integer idEmpresa;
    private String nombre;
    private String cif;
    private String direccion;
    private String telefono;
    private String emailContacto;
    private String logoUrl;
}
