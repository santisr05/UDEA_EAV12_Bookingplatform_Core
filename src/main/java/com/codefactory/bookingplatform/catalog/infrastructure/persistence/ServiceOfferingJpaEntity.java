package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "catalog_service")
public class ServiceOfferingJpaEntity {
    @Id
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "preparation_minutes", nullable = false)
    private int preparationMinutes;

    @Column(name = "cleanup_minutes", nullable = false)
    private int cleanupMinutes;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "currency_id", nullable = false)
    private UUID currencyId;

    @Column(name = "capacity_type", nullable = false, length = 20)
    private String capacityType;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @Column(nullable = false, length = 20)
    private String status;

    protected ServiceOfferingJpaEntity() {
    }

    public ServiceOfferingJpaEntity(UUID id, UUID providerId, String name, String description, int durationMinutes,
                                     int preparationMinutes, int cleanupMinutes, BigDecimal price, UUID currencyId,
                                     String capacityType, int maxCapacity, String status) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.preparationMinutes = preparationMinutes;
        this.cleanupMinutes = cleanupMinutes;
        this.price = price;
        this.currencyId = currencyId;
        this.capacityType = capacityType;
        this.maxCapacity = maxCapacity;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getProviderId() { return providerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getPreparationMinutes() { return preparationMinutes; }
    public int getCleanupMinutes() { return cleanupMinutes; }
    public BigDecimal getPrice() { return price; }
    public UUID getCurrencyId() { return currencyId; }
    public String getCapacityType() { return capacityType; }
    public int getMaxCapacity() { return maxCapacity; }
    public String getStatus() { return status; }
}
