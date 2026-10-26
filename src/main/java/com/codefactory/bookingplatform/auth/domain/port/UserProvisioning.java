package com.codefactory.bookingplatform.auth.domain.port;

import java.util.UUID;

/**
 * Fachada publica del modulo auth que identity (y catalog, si llega a
 * necesitarlo) puede llamar. Es la UNICA puerta de entrada permitida hacia
 * auth desde otros modulos: nunca se llama a auth.infrastructure ni a
 * auth.api directamente (regla verificada en ArchitectureTest).
 */
public interface UserProvisioning {
    UUID provisionUser(String email, String password);
}
