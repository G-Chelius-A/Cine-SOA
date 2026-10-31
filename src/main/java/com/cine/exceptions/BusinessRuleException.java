package com.cine.exceptions;

// AGREGADO [64]: Excepción para violación de reglas de negocio | MOTIVO: Regla de negocio 5 (capacidad máxima de sala) y 6 (asiento único por función)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}

