package com.codefactory.bookingplatform.shared.error;

/**
 * Catalogo de codigos de error estables de la API. El valor de cada constante
 * es el que viaja en el campo "errorCode" de la respuesta ProblemDetail y no
 * debe cambiar entre versiones, para que los clientes puedan discriminar por el.
 */
public enum ErrorCode {
    VALIDATION_ERROR,
    DUPLICATE_RESOURCE,
    NOT_FOUND,
    INVALID_STATE_TRANSITION,
    BUSINESS_RULE_VIOLATION,
    AUTH_REQUIRED,
    AUTH_INVALID_CREDENTIALS,
    AUTH_ACCOUNT_LOCKED,
    FORBIDDEN,
    RESOURCE_UNAVAILABLE,
    CONFLICT
}
