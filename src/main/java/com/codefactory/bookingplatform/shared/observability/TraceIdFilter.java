package com.codefactory.bookingplatform.shared.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Asigna un traceId unico a cada peticion y lo deja disponible en el MDC
 * (para que aparezca en cada linea de log de esa peticion) y en un
 * ThreadLocal propio (para que GlobalExceptionHandler lo pueda leer sin
 * acoplarse a la libreria de logging).
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Trace-Id";
    private static final String MDC_KEY = "traceId";
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    public static String currentTraceId() {
        String value = CURRENT.get();
        return value != null ? value : "unknown";
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString();
        CURRENT.set(traceId);
        MDC.put(MDC_KEY, traceId);
        response.setHeader(HEADER, traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
            CURRENT.remove();
        }
    }
}
