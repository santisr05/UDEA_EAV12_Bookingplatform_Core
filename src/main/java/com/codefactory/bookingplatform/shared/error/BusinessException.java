package com.codefactory.bookingplatform.shared.error;

import org.springframework.http.HttpStatus;

/**
 * Excepcion base para violaciones de reglas de negocio detectadas en el
 * dominio o en los casos de uso. El GlobalExceptionHandler la traduce a un
 * ProblemDetail con el status y el errorCode indicados aqui.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;

    public BusinessException(ErrorCode errorCode, HttpStatus status, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, message);
    }

    public static BusinessException duplicate(String message) {
        return new BusinessException(ErrorCode.DUPLICATE_RESOURCE, HttpStatus.CONFLICT, message);
    }

    public static BusinessException invalidState(String message) {
        return new BusinessException(ErrorCode.INVALID_STATE_TRANSITION, HttpStatus.CONFLICT, message);
    }

    public static BusinessException rule(String message) {
        return new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION, HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    public static BusinessException unavailable(String message) {
        return new BusinessException(ErrorCode.RESOURCE_UNAVAILABLE, HttpStatus.CONFLICT, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN, message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
