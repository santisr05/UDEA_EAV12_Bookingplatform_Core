package com.codefactory.bookingplatform.scheduling.domain.port;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * HU-010. Cada franja discreta que ocupa un recurso se guarda como una fila
 * propia con UNIQUE(resource_id, slot_start); es la clave para que MySQL
 * rechace, de forma atomica, una asignacion concurrente del mismo recurso en
 * el mismo instante (equivalente discretizado al EXCLUDE de PostgreSQL).
 */
public interface ResourceSlotLockRepository {

    /** Lanza DataIntegrityViolationException si algun slot ya esta tomado. */
    void lockSlots(UUID resourceId, UUID bookingId, List<Instant> slotStarts);

    void releaseSlots(UUID bookingId);

    boolean hasAnyLockInRange(UUID resourceId, Instant start, Instant end);
}
