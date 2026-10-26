package com.codefactory.bookingplatform.catalog.domain.port;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto que implementa scheduling: la proxima franja disponible de un
 * servicio en los siguientes 30 dias (HU-005, "Servicio sin disponibilidad
 * proxima").
 */
public interface NextAvailableSlotQuery {
    Optional<Instant> nextAvailableSlot(UUID serviceId);
}
