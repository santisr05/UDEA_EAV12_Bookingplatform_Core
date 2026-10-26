package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "service_resource_requirement")
public class ServiceResourceRequirementJpaEntity {
    @Id
    private UUID id;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(name = "resource_type_id", nullable = false)
    private UUID resourceTypeId;

    @Column(name = "quantity_required", nullable = false)
    private int quantityRequired;

    protected ServiceResourceRequirementJpaEntity() {
    }

    public ServiceResourceRequirementJpaEntity(UUID id, UUID serviceId, UUID resourceTypeId, int quantityRequired) {
        this.id = id;
        this.serviceId = serviceId;
        this.resourceTypeId = resourceTypeId;
        this.quantityRequired = quantityRequired;
    }

    public UUID getId() { return id; }
    public UUID getServiceId() { return serviceId; }
    public UUID getResourceTypeId() { return resourceTypeId; }
    public int getQuantityRequired() { return quantityRequired; }
}
