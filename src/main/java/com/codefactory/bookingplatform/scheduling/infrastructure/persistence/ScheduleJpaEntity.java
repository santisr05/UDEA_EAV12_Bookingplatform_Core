package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "schedule")
public class ScheduleJpaEntity {
    @Id
    private UUID id;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(name = "granularity_minutes", nullable = false)
    private int granularityMinutes;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(nullable = false, length = 20)
    private String status;

    protected ScheduleJpaEntity() {
    }

    public ScheduleJpaEntity(UUID id, UUID locationId, UUID resourceId, int granularityMinutes, LocalDate validFrom,
                              LocalDate validTo, String status) {
        this.id = id;
        this.locationId = locationId;
        this.resourceId = resourceId;
        this.granularityMinutes = granularityMinutes;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getLocationId() { return locationId; }
    public UUID getResourceId() { return resourceId; }
    public int getGranularityMinutes() { return granularityMinutes; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public String getStatus() { return status; }
}
