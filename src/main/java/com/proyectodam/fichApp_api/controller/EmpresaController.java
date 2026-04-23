package com.proyectodam.fichApp_api.controller;

import com.proyectodam.fichApp_api.dto.EmpresaDTO;
import com.proyectodam.fichApp_api.service.IEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaController {

    @Autowired
    private IEmpresaService empresaService;

    @GetMapping("/config")
    public ResponseEntity<EmpresaDTO> getEmpresaConfig() {
        return ResponseEntity.ok(empresaService.getEmpresaConfig());
    }

    @PutMapping("/config")
    public ResponseEntity<EmpresaDTO> updateEmpresaConfig(@RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.updateEmpresaConfig(empresaDTO));
    }
}
