package com.codefactory.bookingplatform.scheduling.domain.port;

import java.time.Instant;
import java.util.UUID;

/**
 * Puerto que scheduling define pero que satisface booking (inversion de
 * dependencia): le permite a scheduling saber si una agenda o un recurso
 * tienen reservas futuras confirmadas antes de aplicar un cambio que las
 * afectaria (HU-006, HU-007, HU-009).
 */
public interface FutureBookingsQuery {
    long countFutureConfirmedBookingsForResource(UUID resourceId);
    long countFutureConfirmedBookingsForSchedule(UUID scheduleId);
    long countFutureConfirmedBookingsForScheduleInRange(UUID scheduleId, Instant from, Instant to);
}
