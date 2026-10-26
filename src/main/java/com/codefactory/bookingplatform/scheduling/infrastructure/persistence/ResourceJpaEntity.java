package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "resource")
public class ResourceJpaEntity {
    @Id
    private UUID id;

    @Column(name = "resource_type_id", nullable = false)
    private UUID resourceTypeId;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false, length = 20)
    private String status;

    protected ResourceJpaEntity() {
    }

    public ResourceJpaEntity(UUID id, UUID resourceTypeId, UUID locationId, String name, int capacity, String status) {
        this.id = id;
        this.resourceTypeId = resourceTypeId;
        this.locationId = locationId;
        this.name = name;
        this.capacity = capacity;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getResourceTypeId() { return resourceTypeId; }
    public UUID getLocationId() { return locationId; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }
}
