package com.accenture.dominio.excepcion;

public class ErrorNotFound extends RuntimeException {
    public ErrorNotFound(String message) {
        super(message);
    }
}
