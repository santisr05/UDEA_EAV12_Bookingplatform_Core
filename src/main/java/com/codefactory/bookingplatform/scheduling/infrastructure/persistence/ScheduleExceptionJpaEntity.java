package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "schedule_exception")
public class ScheduleExceptionJpaEntity {
    @Id
    private UUID id;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Column(length = 200)
    private String reason;

    @Column(nullable = false)
    private boolean recurring;

    protected ScheduleExceptionJpaEntity() {
    }

    public ScheduleExceptionJpaEntity(UUID id, UUID scheduleId, String type, Instant startsAt, Instant endsAt,
                                       String reason, boolean recurring) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.type = type;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.reason = reason;
        this.recurring = recurring;
    }

    public UUID getId() { return id; }
    public UUID getScheduleId() { return scheduleId; }
    public String getType() { return type; }
    public Instant getStartsAt() { return startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public String getReason() { return reason; }
    public boolean isRecurring() { return recurring; }
}
