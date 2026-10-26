package com.codefactory.bookingplatform.catalog.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * HU-004. Se llama ServiceOffering (y no "Service") para no chocar con el
 * estereotipo @Service de Spring en la capa de aplicacion.
 */
public class ServiceOffering {

    private final UUID id;
    private final UUID providerId;
    private String name;
    private String description;
    private int durationMinutes;
    private int preparationMinutes;
    private int cleanupMinutes;
    private BigDecimal price;
    private UUID currencyId;
    private CapacityType capacityType;
    private int maxCapacity;
    private ServiceStatus status;

    private ServiceOffering(UUID id, UUID providerId, String name, String description, int durationMinutes,
                             int preparationMinutes, int cleanupMinutes, BigDecimal price, UUID currencyId,
                             CapacityType capacityType, int maxCapacity, ServiceStatus status) {
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

    public static ServiceOffering create(UUID id, UUID providerId, String name, String description,
                                          int durationMinutes, int preparationMinutes, int cleanupMinutes,
                                          BigDecimal price, UUID currencyId, CapacityType capacityType, int maxCapacity) {
        if (name == null || name.isBlank()) {
            throw BusinessException.rule("El nombre del servicio es obligatorio");
        }
        if (durationMinutes <= 0) {
            throw BusinessException.rule("La duracion del servicio debe ser mayor a cero");
        }
        if (price == null || price.signum() < 0) {
            throw BusinessException.rule("El precio del servicio no puede ser negativo");
        }
        if (capacityType == CapacityType.GROUP && maxCapacity <= 1) {
            throw BusinessException.rule("Un servicio grupal debe tener un cupo maximo mayor a 1");
        }
        if (capacityType == CapacityType.INDIVIDUAL) {
            maxCapacity = 1;
        }
        return new ServiceOffering(id, providerId, name, description, durationMinutes, preparationMinutes,
                cleanupMinutes, price, currencyId, capacityType, maxCapacity, ServiceStatus.ACTIVE);
    }

    public static ServiceOffering rehydrate(UUID id, UUID providerId, String name, String description,
                                             int durationMinutes, int preparationMinutes, int cleanupMinutes,
                                             BigDecimal price, UUID currencyId, CapacityType capacityType,
                                             int maxCapacity, ServiceStatus status) {
        return new ServiceOffering(id, providerId, name, description, durationMinutes, preparationMinutes,
                cleanupMinutes, price, currencyId, capacityType, maxCapacity, status);
    }

    public void updateSchedule(int durationMinutes, int preparationMinutes, int cleanupMinutes) {
        // HU-006, escenario "Cambio de duracion no afecta reservas existentes":
        // este cambio no toca reservas ya creadas porque cada una congela su
        // propia duracion en booking_condition_snapshot.
        if (durationMinutes <= 0) {
            throw BusinessException.rule("La duracion del servicio debe ser mayor a cero");
        }
        this.durationMinutes = durationMinutes;
        this.preparationMinutes = preparationMinutes;
        this.cleanupMinutes = cleanupMinutes;
    }

    public void deactivate() {
        this.status = ServiceStatus.INACTIVE;
    }

    public void activate() {
        this.status = ServiceStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == ServiceStatus.ACTIVE;
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
    public CapacityType getCapacityType() { return capacityType; }
    public int getMaxCapacity() { return maxCapacity; }
    public ServiceStatus getStatus() { return status; }
}
