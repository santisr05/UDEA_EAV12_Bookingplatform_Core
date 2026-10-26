package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "booking_status_change")
public class BookingStatusChangeJpaEntity {
    @Id
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "from_status", nullable = false, length = 20)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 20)
    private String toStatus;

    @Column(length = 200)
    private String reason;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected BookingStatusChangeJpaEntity() {
    }

    public BookingStatusChangeJpaEntity(UUID id, UUID bookingId, String fromStatus, String toStatus, String reason,
                                         UUID changedBy, Instant changedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.reason = reason;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }
}
