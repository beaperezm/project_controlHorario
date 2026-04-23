package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.dto.EmpresaDTO;
import com.proyectodam.fichApp_api.model.Empresa;
import com.proyectodam.fichApp_api.repository.EmpresaRepository;
import com.proyectodam.fichApp_api.service.IEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaServiceImpl implements IEmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Override
    public EmpresaDTO getEmpresaConfig() {
        List<Empresa> empresas = empresaRepository.findAll();
        Empresa empresa;
        
        if (empresas.isEmpty()) {
            // Crear una configuración por defecto si la base de datos está totalmente vacía
            empresa = new Empresa();
            empresa.setNombre("Nueva Empresa");
            empresa.setCif("B00000000"); 
            empresa.setDireccion("");
            empresa.setTelefono("");
            empresa.setEmailContacto("");
            empresa = empresaRepository.save(empresa);
        } else {
            empresa = empresas.get(0); // Asumimos que solo existe una configuración de empresa
        }
        
        return toDTO(empresa);
    }

    @Override
    public EmpresaDTO updateEmpresaConfig(EmpresaDTO empresaDTO) {
        List<Empresa> empresas = empresaRepository.findAll();
        Empresa empresa;
        
        if (empresas.isEmpty()) {
            empresa = new Empresa();
        } else {
            empresa = empresas.get(0);
        }
        
        if (empresaDTO.getNombre() != null) empresa.setNombre(empresaDTO.getNombre());
        if (empresaDTO.getCif() != null) empresa.setCif(empresaDTO.getCif());
        if (empresaDTO.getDireccion() != null) empresa.setDireccion(empresaDTO.getDireccion());
        if (empresaDTO.getTelefono() != null) empresa.setTelefono(empresaDTO.getTelefono());
        if (empresaDTO.getEmailContacto() != null) empresa.setEmailContacto(empresaDTO.getEmailContacto());
        if (empresaDTO.getLogoUrl() != null) empresa.setLogoUrl(empresaDTO.getLogoUrl());
        
        empresa = empresaRepository.save(empresa);
        
        return toDTO(empresa);
    }
    
    private EmpresaDTO toDTO(Empresa empresa) {
        return new EmpresaDTO(
            empresa.getIdEmpresa(),
            empresa.getNombre(),
            empresa.getCif(),
            empresa.getDireccion(),
            empresa.getTelefono(),
            empresa.getEmailContacto(),
            empresa.getLogoUrl()
        );
    }
}
