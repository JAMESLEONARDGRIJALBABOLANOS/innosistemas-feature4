package com.udea.innosistemas.exception;

/**
 * Excepción personalizada para errores de lógica de negocio
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

