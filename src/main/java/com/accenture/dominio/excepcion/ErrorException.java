package com.accenture.dominio.excepcion;

public class ErrorException extends RuntimeException {
    public ErrorException (String message) {
            super(message);
        }

}
