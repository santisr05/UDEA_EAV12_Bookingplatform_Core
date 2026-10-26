package com.codefactory.bookingplatform.catalog.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.util.UUID;

public class ResourceType {

    private final UUID id;
    private final UUID providerId;
    private String name;
    private String description;

    private ResourceType(UUID id, UUID providerId, String name, String description) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.description = description;
    }

    public static ResourceType create(UUID id, UUID providerId, String name, String description) {
        if (name == null || name.isBlank()) {
            throw BusinessException.rule("El nombre del tipo de recurso es obligatorio");
        }
        return new ResourceType(id, providerId, name, description);
    }

    public static ResourceType rehydrate(UUID id, UUID providerId, String name, String description) {
        return new ResourceType(id, providerId, name, description);
    }

    public UUID getId() { return id; }
    public UUID getProviderId() { return providerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
