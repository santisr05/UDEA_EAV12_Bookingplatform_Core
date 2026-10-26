package com.codefactory.bookingplatform.scheduling.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.util.UUID;

/**
 * HU-009 (instancias concretas de recurso). El tipo de recurso (ResourceType)
 * vive en catalog; aqui vive la instancia fisica que participa en la
 * disponibilidad y asignacion (HU-010).
 */
public class Resource {

    private final UUID id;
    private final UUID resourceTypeId;
    private final UUID locationId;
    private String name;
    private int capacity;
    private ResourceStatus status;

    private Resource(UUID id, UUID resourceTypeId, UUID locationId, String name, int capacity, ResourceStatus status) {
        this.id = id;
        this.resourceTypeId = resourceTypeId;
        this.locationId = locationId;
        this.name = name;
        this.capacity = capacity;
        this.status = status;
    }

    public static Resource create(UUID id, UUID resourceTypeId, UUID locationId, String name, int capacity) {
        if (name == null || name.isBlank()) {
            throw BusinessException.rule("El nombre del recurso es obligatorio");
        }
        if (capacity <= 0) {
            throw BusinessException.rule("La capacidad del recurso debe ser mayor a cero");
        }
        return new Resource(id, resourceTypeId, locationId, name, capacity, ResourceStatus.AVAILABLE);
    }

    public static Resource rehydrate(UUID id, UUID resourceTypeId, UUID locationId, String name, int capacity,
                                      ResourceStatus status) {
        return new Resource(id, resourceTypeId, locationId, name, capacity, status);
    }

    public void retire() {
        this.status = ResourceStatus.RETIRED;
    }

    public UUID getId() { return id; }
    public UUID getResourceTypeId() { return resourceTypeId; }
    public UUID getLocationId() { return locationId; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public ResourceStatus getStatus() { return status; }
}
