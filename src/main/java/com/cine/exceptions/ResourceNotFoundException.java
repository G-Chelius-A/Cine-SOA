package com.cine.exceptions;

// AGREGADO [63]: Excepción de recurso no encontrado | MOTIVO: Manejo de errores semánticos para entidades no existentes con respuesta 404
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

