package com.codefactory.bookingplatform.identity.domain.port;

import java.util.UUID;

/**
 * Puerto que identity define pero no implementa: lo satisface una clase de
 * infraestructura del modulo booking (inversion de dependencia), para que
 * identity pueda preguntar "tiene reservas futuras confirmadas" sin depender
 * del modulo booking en tiempo de compilacion.
 */
public interface FutureConfirmedBookingsQuery {
    long countFutureConfirmedBookings(UUID providerId);
}
