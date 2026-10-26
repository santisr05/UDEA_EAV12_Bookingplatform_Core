package com.codefactory.bookingplatform.catalog.domain.port;

import com.codefactory.bookingplatform.catalog.domain.model.ResourceType;

import java.util.Optional;
import java.util.UUID;

public interface ResourceTypeRepository {
    ResourceType save(ResourceType resourceType);
    Optional<ResourceType> findById(UUID id);
    boolean existsByProviderIdAndName(UUID providerId, String name);
}
