package com.proyectodam.fichApp_api.service.impl;

import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.enums.EstadoFirma;
import com.proyectodam.fichApp_api.model.Documento;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.repository.DocumentoRepository;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import com.proyectodam.fichApp_api.repository.ContratoRepository;
import com.proyectodam.fichApp_api.model.Contrato;
import com.proyectodam.fichApp_api.service.IDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements IDocumentoService {

    private final DocumentoRepository documentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ContratoRepository contratoRepository;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    public DocumentoDTO subirDocumento(MultipartFile archivo, String nombreCustom, String categoria,
            Integer idEmpleado) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        String originalName = StringUtils.cleanPath(archivo.getOriginalFilename());
        String ext = "";
        int dotIdx = originalName.lastIndexOf('.');
        if (dotIdx > 0) {
            ext = originalName.substring(dotIdx);
        }

        String finalName = originalName;
        if (nombreCustom != null && !nombreCustom.trim().isEmpty()) {
            finalName = nombreCustom.trim();
            if (!finalName.toLowerCase().endsWith(ext.toLowerCase())) {
                finalName += ext;
            }
        }

        try {
            String hash = calcularHash(archivo.getBytes());
            if (documentoRepository.existsByHashDocumento(hash)) {
                throw new RuntimeException("El documento ya existe en el sistema (Hash duplicado).");
            }

            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }

            Path targetLocation = fileStorageLocation.resolve(finalName);
            Files.copy(archivo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Documento documento = Documento.builder()
                    .nombreArchivo(finalName)
                    .rutaAcceso(targetLocation.toString())
                    .tipoMime(archivo.getContentType())
                    .tamanoBytes(archivo.getSize())
                    .categoria(categoria)
                    .estadoFirma(EstadoFirma.PENDIENTE)
                    .hashDocumento(hash)
                    .empleado(empleado)
                    .build();

            Documento guardado = documentoRepository.save(documento);
            return mapToDTO(guardado);
        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error al almacenar el archivo " + finalName, ex);
        }
    }

    @Override
    public DocumentoDTO obtenerDetalles(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        return mapToDTO(documento);
    }

    @Override
    public List<DocumentoDTO> listarTodos() {
        return documentoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentoDTO> listarPorEmpleado(Integer idEmpleado) {
        return documentoRepository.findByEmpleadoIdEmpleado(idEmpleado).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] descargarContenido(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        try {
            Path filePath = Paths.get(documento.getRutaAcceso());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo", e);
        }
    }

    @Override
    public void firmarDocumento(Long id, Integer idEmpleado) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        if (!java.util.Objects.equals(documento.getEmpleado().getIdEmpleado(), idEmpleado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para firmar este documento");
        }

        documento.setEstadoFirma(EstadoFirma.FIRMADO);
        documento.setFechaFirma(LocalDateTime.now());
        documentoRepository.save(documento);
    }

    @Override
    public void eliminarDocumento(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        try {
            Path filePath = Paths.get(documento.getRutaAcceso());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo borrar el archivo físico " + documento.getRutaAcceso());
        }

        documentoRepository.delete(documento);
    }

    private String calcularHash(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(content);
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private DocumentoDTO mapToDTO(Documento documento) {
        String departamento = "Desconocido";
        if (documento.getEmpleado() != null) {
            List<Contrato> contratos = contratoRepository
                    .findByEmpleado_IdEmpleado(documento.getEmpleado().getIdEmpleado());
            if (!contratos.isEmpty() && contratos.get(0).getDepartamento() != null) {
                departamento = contratos.get(0).getDepartamento().getNombre();
            }
        }

        return DocumentoDTO.builder()
                .id(documento.getId())
                .nombreArchivo(documento.getNombreArchivo())
                .tipoMime(documento.getTipoMime())
                .tamanoBytes(documento.getTamanoBytes())
                .categoria(documento.getCategoria())
                .fechaSubida(documento.getFechaSubida())
                .urlDescarga("/api/documentos/" + documento.getId() + "/download")
                .idEmpleado(documento.getEmpleado() != null ? documento.getEmpleado().getIdEmpleado() : null)
                .nombreEmpleado(documento.getEmpleado() != null
                        ? documento.getEmpleado().getNombre() + " " + documento.getEmpleado().getApellidos()
                        : "Desconocido")
                .departamento(departamento)
                .build();
    }
}
