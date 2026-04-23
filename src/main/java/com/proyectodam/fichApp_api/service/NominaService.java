package com.proyectodam.fichApp_api.service;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import com.proyectodam.fichApp_api.enums.EstadoFirma;
import com.proyectodam.fichApp_api.model.Documento;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.repository.DocumentoRepository;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NominaService {

    private final IDocumentoService documentoService;
    private final DocumentoRepository documentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public DocumentoDTO uploadNomina(MultipartFile archivo, String mesAnio, Integer idEmpleado) {
        String nombreCustom = "Nomina_" + mesAnio + ".pdf";
        Integer anio = null;
        Integer mes = null;

        // Intentar parsear mesAnio (formatos esperados: "MM-YYYY" o "YYYY-MM")
        if (mesAnio != null && mesAnio.contains("-")) {
            String[] parts = mesAnio.split("-");
            try {
                if (parts[0].length() == 4) { // YYYY-MM
                    anio = Integer.parseInt(parts[0]);
                    mes = Integer.parseInt(parts[1]);
                } else { // MM-YYYY
                    mes = Integer.parseInt(parts[0]);
                    anio = Integer.parseInt(parts[1]);
                }
            } catch (NumberFormatException ignored) {}
        }

        return documentoService.subirDocumento(archivo, nombreCustom, CategoriaDocumento.NOMINA.name(), idEmpleado, anio, mes, "NÓMINA");
    }

    public void signNomina(Long nominaId, Integer idEmpleado, String rawPassword) {
        Documento documento = documentoRepository.findById(nominaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nómina no encontrada"));

        if (!documento.getCategoria().equals(CategoriaDocumento.NOMINA.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El documento no es una nómina");
        }

        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        if (!java.util.Objects.equals(documento.getEmpleado().getIdEmpleado(), idEmpleado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para firmar esta nómina");
        }

        if (!passwordEncoder.matches(rawPassword, empleado.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta, no se puede firmar");
        }

        documento.setEstadoFirma(EstadoFirma.FIRMADO);
        documento.setFechaFirma(LocalDateTime.now());
        documentoRepository.save(documento);
    }

    public void sendEmail(Long nominaId) {
        Documento documento = documentoRepository.findById(nominaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nómina no encontrada"));

        if (!documento.getCategoria().equals(CategoriaDocumento.NOMINA.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El documento no es una nómina");
        }

        Empleado empleado = documento.getEmpleado();
        if (empleado.getEmail() != null && !empleado.getEmail().isEmpty()) {
            emailService.sendNominaNotification(empleado.getEmail(), documento.getNombreArchivo());
            documento.setEstadoFirma(EstadoFirma.NOTIFICADA);
            documentoRepository.save(documento);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El empleado no tiene email configurado");
        }
    }
}
