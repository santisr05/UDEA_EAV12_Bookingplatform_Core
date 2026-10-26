package com.codefactory.bookingplatform.shared.error;

import com.codefactory.bookingplatform.shared.observability.TraceIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de errores. Toda respuesta de error sigue el formato
 * application/problem+json (RFC 9457), con errorCode y traceId para
 * correlacionar con los logs del servidor (ver ADR-04 del proyecto original).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TYPE_BASE = "https://bookingplatform.codefactory.com/errors/";

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex, HttpServletRequest request) {
        return build(ex.getStatus(), ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .collect(Collectors.toList());
        ProblemDetail detail = build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR,
                "Uno o mas campos no cumplen las validaciones requeridas", request, null);
        detail.setProperty("fieldErrors", fieldErrors);
        return detail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.debug("Violacion de integridad de datos", ex);
        return build(HttpStatus.CONFLICT, ErrorCode.CONFLICT,
                "La operacion viola una restriccion de datos (por ejemplo, un valor duplicado)", request, null);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ProblemDetail handleUnauthenticated(InsufficientAuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_REQUIRED, "Authentication is required", request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleForbidden(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "No tiene permisos para esta operacion", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.BUSINESS_RULE_VIOLATION,
                "Ocurrio un error inesperado", request, null);
    }

    private Map<String, String> toFieldError(FieldError error) {
        return Map.of("field", error.getField(), "message", String.valueOf(error.getDefaultMessage()));
    }

    private ProblemDetail build(HttpStatus status, ErrorCode code, String detail, HttpServletRequest request, String title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title != null ? title : status.getReasonPhrase());
        problemDetail.setType(URI.create(TYPE_BASE + code.name().toLowerCase().replace('_', '_')));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("errorCode", code.name());
        problemDetail.setProperty("traceId", TraceIdFilter.currentTraceId());
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }
}
