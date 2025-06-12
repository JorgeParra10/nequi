package com.accenture.aplicacion.exceptiones;

public class ExceptionResponse {
    private int statusCode;
    private String message;
    private String path;
    private String method;
    private String timestamp;

    public ExceptionResponse() {
    }

    public ExceptionResponse(int statusCode, String message, String path, String method, String timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.method = method;
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
