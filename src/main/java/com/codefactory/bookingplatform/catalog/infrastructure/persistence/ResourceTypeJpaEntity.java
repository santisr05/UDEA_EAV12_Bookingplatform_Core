package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "resource_type")
public class ResourceTypeJpaEntity {
    @Id
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;

    protected ResourceTypeJpaEntity() {
    }

    public ResourceTypeJpaEntity(UUID id, UUID providerId, String name, String description) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.description = description;
    }

    public UUID getId() { return id; }
    public UUID getProviderId() { return providerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
