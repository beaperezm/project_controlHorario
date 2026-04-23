package com.proyectodam.fichApp_api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 * Intercepta excepciones comunes y devuelve respuestas JSON claras
 * en lugar de errores 500 genéricos.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja intentos de insertar/actualizar campos únicos duplicados.
     */
    @ExceptionHandler(DuplicateFieldException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateField(DuplicateFieldException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex.getFieldName());
    }

    /**
     * Maneja recursos no encontrados (empleados, empresas, etc.).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * Captura errores de integridad de la base de datos (constraint violations)
     * que no fueron capturados previamente por validaciones manuales.
     * Esto convierte los errores 500 crípticos en mensajes legibles.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String mensaje = extraerMensajeAmigable(ex);
        return buildErrorResponse(HttpStatus.CONFLICT, mensaje, null);
    }

    /**
     * Captura cualquier otra RuntimeException no manejada.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    // Utilidades 

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, String field) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (field != null) {
            body.put("field", field);
        }
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Intenta extraer un mensaje legible de una DataIntegrityViolationException.
     * Analiza el mensaje de la causa raíz para identificar qué campo causó el conflicto.
     */
    private String extraerMensajeAmigable(DataIntegrityViolationException ex) {
        String rootMsg = ex.getMostSpecificCause().getMessage().toLowerCase();

        // Email duplicado
        if (rootMsg.contains("email")) {
            return "Ya existe un empleado con ese correo electrónico.";
        }
        // DNI/NIE duplicado
        if (rootMsg.contains("dni_nie") || rootMsg.contains("dni")) {
            return "Ya existe un empleado con ese DNI/NIE.";
        }
        // NUSS duplicado
        if (rootMsg.contains("nuss")) {
            return "Ya existe un empleado con ese número de la Seguridad Social (NUSS).";
        }
        // Auth user ID duplicado (Supabase)
        if (rootMsg.contains("auth_user_id")) {
            return "Este empleado ya tiene una cuenta de autenticación vinculada.";
        }
        // Clave primaria duplicada
        if (rootMsg.contains("primary key") || rootMsg.contains("clave primaria")) {
            return "Error: Se intentó crear un registro con un identificador que ya existe.";
        }
        // Restricción de unicidad genérica
        if (rootMsg.contains("unique") || rootMsg.contains("unicidad") || rootMsg.contains("duplicate")) {
            return "Ya existe un registro con alguno de los datos introducidos. Revise los campos únicos (email, DNI, etc.).";
        }

        // Fallback genérico
        return "Error de integridad de datos. Verifique que no está duplicando información.";
    }
}
