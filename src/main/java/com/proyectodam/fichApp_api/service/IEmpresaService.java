package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.EmpresaDTO;

public interface IEmpresaService {
    EmpresaDTO getEmpresaConfig();
    EmpresaDTO updateEmpresaConfig(EmpresaDTO empresaDTO);
}
