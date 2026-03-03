package com.proyectodam.fichApp_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectodam.fichApp_api.dto.DocumentoDTO;
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
@Transactional // Revertir después de cada prueba
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
                // Crear un empleado de prueba
                testEmpleado = new Empleado();
                testEmpleado.setNombre("Test");
                testEmpleado.setApellidos("User");
                testEmpleado.setEmail("test@example.com");
                testEmpleado.setTelefono("123456789");
                testEmpleado.setDniNie("12345678A");
                testEmpleado.setFechaNacimiento(LocalDate.of(1990, 1, 1));
                testEmpleado.setGenero(TipoGenero.M);
                testEmpleado.setEstado(EstadoEmpleado.ACTIVO);
                testEmpleado.setPasswordHash("hash"); // Ficticio
                testEmpleado = empleadoRepository.save(testEmpleado);
        }

        @Test
        void testFullDocumentFlow() throws Exception {
                // 1. Subir documento
                MockMultipartFile file = new MockMultipartFile(
                                "archivo",
                                "test-doc.pdf",
                                MediaType.APPLICATION_PDF_VALUE,
                                "Test Content".getBytes());

                MvcResult uploadResult = mockMvc.perform(multipart("/api/documentos/upload")
                                .file(file)
                                .param("categoria", "NÓMINA")
                                .param("idEmpleado", String.valueOf(testEmpleado.getIdEmpleado())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombreArchivo").value("test-doc.pdf"))
                                .andExpect(jsonPath("$.idEmpleado").value(testEmpleado.getIdEmpleado()))
                                .andReturn();

                DocumentoDTO uploadedDoc = objectMapper.readValue(uploadResult.getResponse().getContentAsString(),
                                DocumentoDTO.class);
                Long docId = uploadedDoc.getId();

                // 2. Listar documentos del empleado
                mockMvc.perform(get("/api/documentos/empleado/" + testEmpleado.getIdEmpleado()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(docId));

                // 3. Firmar documento
                mockMvc.perform(put("/api/documentos/" + docId + "/firmar")
                                .param("idEmpleado", String.valueOf(testEmpleado.getIdEmpleado()))) // Propietario
                                                                                                    // correcto
                                .andExpect(status().isOk());

                // 4. Verificar estado firmado (Volver a obtener detalles)
                // Nota: Es posible que necesite actualizar el DTO para devolver el estado o
                // revisar la entidad directamente,
                // pero aquí solo verificamos si la llamada anterior tuvo éxito.

                // 5. Intentar firmar con un empleado incorrecto (debería fallar)
                mockMvc.perform(put("/api/documentos/" + docId + "/firmar")
                                .param("idEmpleado", "99999")) // ID inexistente o incorrecto
                                .andExpect(status().is4xxClientError()) // Asumiendo 400/404/500 dependiendo del manejo
                                .andReturn();

                // 6. Eliminar documento
                mockMvc.perform(delete("/api/documentos/" + docId))
                                .andExpect(status().isNoContent());
        }
}
