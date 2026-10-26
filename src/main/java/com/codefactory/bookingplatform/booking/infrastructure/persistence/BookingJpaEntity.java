package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class BookingJpaEntity {
    @Id
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Column(nullable = false)
    private int attendees;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "reschedule_count", nullable = false)
    private int rescheduleCount;

    @Column(name = "manual_confirmation_required", nullable = false)
    private boolean manualConfirmationRequired;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected BookingJpaEntity() {
    }

    public BookingJpaEntity(UUID id, UUID clientId, UUID serviceId, UUID locationId, String code, Instant startsAt,
                             Instant endsAt, int attendees, BigDecimal totalPrice, String status, int rescheduleCount,
                             boolean manualConfirmationRequired, Instant createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.serviceId = serviceId;
        this.locationId = locationId;
        this.code = code;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.attendees = attendees;
        this.totalPrice = totalPrice;
        this.status = status;
        this.rescheduleCount = rescheduleCount;
        this.manualConfirmationRequired = manualConfirmationRequired;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public UUID getServiceId() { return serviceId; }
    public UUID getLocationId() { return locationId; }
    public String getCode() { return code; }
    public Instant getStartsAt() { return startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public int getAttendees() { return attendees; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public int getRescheduleCount() { return rescheduleCount; }
    public boolean isManualConfirmationRequired() { return manualConfirmationRequired; }
    public Instant getCreatedAt() { return createdAt; }
}
