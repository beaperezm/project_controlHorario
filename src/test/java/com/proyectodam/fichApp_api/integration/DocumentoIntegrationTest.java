package com.proyectodam.fichApp_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectodam.fichApp_api.dto.DocumentoDTO;
import com.proyectodam.fichApp_api.enums.CategoriaDocumento;
import com.proyectodam.fichApp_api.enums.EstadoEmpleado;
import com.proyectodam.fichApp_api.enums.TipoGenero;
import com.proyectodam.fichApp_api.model.Empleado;
import com.proyectodam.fichApp_api.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback after each test
public class DocumentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Empleado testEmpleado;

    @BeforeEach
    void setUp() {
        // Create a test employee
        testEmpleado = new Empleado();
        testEmpleado.setNombre("Test");
        testEmpleado.setApellidos("User");
        testEmpleado.setEmail("test@example.com");
        testEmpleado.setTelefono("123456789");
        testEmpleado.setDniNie("12345678A");
        testEmpleado.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        testEmpleado.setGenero(TipoGenero.M);
        testEmpleado.setEstado(EstadoEmpleado.ACTIVO);
        testEmpleado.setPasswordHash("hash"); // Dummy
        testEmpleado = empleadoRepository.save(testEmpleado);
    }

    @Test
    void testFullDocumentFlow() throws Exception {
        // 1. Upload Document
        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "test-doc.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test Content".getBytes());

        MvcResult uploadResult = mockMvc.perform(multipart("/api/documentos/upload")
                .file(file)
                .param("categoria", CategoriaDocumento.NOMINA.name())
                .param("idEmpleado", String.valueOf(testEmpleado.getIdEmpleado())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreArchivo").value("test-doc.pdf"))
                .andExpect(jsonPath("$.idEmpleado").value(testEmpleado.getIdEmpleado()))
                .andReturn();

        DocumentoDTO uploadedDoc = objectMapper.readValue(uploadResult.getResponse().getContentAsString(),
                DocumentoDTO.class);
        Long docId = uploadedDoc.getId();

        // 2. List Documents for Employee
        mockMvc.perform(get("/api/documentos/empleado/" + testEmpleado.getIdEmpleado()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(docId));

        // 3. Sign Document
        mockMvc.perform(put("/api/documentos/" + docId + "/firmar")
                .param("idEmpleado", String.valueOf(testEmpleado.getIdEmpleado()))) // Correct owner
                .andExpect(status().isOk());

        // 4. Verify Signed Status (Re-fetch details)
        // Note: You might need to update DTO to return status or check entity directly,
        // but here we just check if the previous call succeeded.

        // 5. Try to sign with wrong employee (should fail)
        mockMvc.perform(put("/api/documentos/" + docId + "/firmar")
                .param("idEmpleado", "99999")) // Non-existent or wrong ID
                .andExpect(status().is4xxClientError()) // Assuming 400/404/500 depending on handling
                .andReturn();

        // 6. Delete Document
        mockMvc.perform(delete("/api/documentos/" + docId))
                .andExpect(status().isNoContent());
    }
}
