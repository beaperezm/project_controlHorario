package com.proyectodam.fichApp_api.exception;

/*
 Excepción lanzada cuando se intenta crear o actualizar un registro
 con un valor que ya existe en un campo que debe ser único (email, DNI, etc.).
 */
public class DuplicateFieldException extends RuntimeException {

    private final String fieldName;
    private final String fieldValue;

    public DuplicateFieldException(String fieldName, String fieldValue) {
        super(String.format("Ya existe un registro con %s: '%s'", fieldName, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
