package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Cada fila es un "slot" discreto ocupado por un recurso. La unicidad real
 * se garantiza con el indice unico (resource_id, slot_start) definido en la
 * migracion Flyway V4, no con la clave primaria de esta entidad.
 */
@Entity
@Table(name = "resource_slot_lock")
public class ResourceSlotLockJpaEntity {
    @Id
    private UUID id;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "slot_start", nullable = false)
    private Instant slotStart;

    protected ResourceSlotLockJpaEntity() {
    }

    public ResourceSlotLockJpaEntity(UUID id, UUID resourceId, UUID bookingId, Instant slotStart) {
        this.id = id;
        this.resourceId = resourceId;
        this.bookingId = bookingId;
        this.slotStart = slotStart;
    }

    public UUID getId() { return id; }
    public UUID getResourceId() { return resourceId; }
    public UUID getBookingId() { return bookingId; }
    public Instant getSlotStart() { return slotStart; }
}
