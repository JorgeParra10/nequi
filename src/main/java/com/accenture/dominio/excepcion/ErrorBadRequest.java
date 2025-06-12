package com.accenture.dominio.excepcion;

public class ErrorBadRequest extends RuntimeException {
    public ErrorBadRequest (String message) {
            super(message);
        }

}
