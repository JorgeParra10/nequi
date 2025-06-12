package com.accenture.dominio.excepcion;

public class ExceptionAlreadyExist extends RuntimeException {
    public ExceptionAlreadyExist (String message) {
            super(message);
        }

}
