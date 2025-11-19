package com.tesig.exception;

/**
 * Excepción para errores de lógica de negocio.
 *
 * Esta excepción se lanza cuando se viola una regla de negocio,
 * como intentar una transición de estado inválida, operaciones
 * sobre entidades en estados incorrectos, etc.
 *
 * Es una RuntimeException para simplificar el manejo de excepciones
 * sin requerir try-catch explícito en cada llamada.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
