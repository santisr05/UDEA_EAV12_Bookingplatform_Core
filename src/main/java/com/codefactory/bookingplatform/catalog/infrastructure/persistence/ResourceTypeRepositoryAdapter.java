package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import com.codefactory.bookingplatform.catalog.domain.model.ResourceType;
import com.codefactory.bookingplatform.catalog.domain.port.ResourceTypeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ResourceTypeRepositoryAdapter implements ResourceTypeRepository {

    private final ResourceTypeJpaRepository jpaRepository;

    public ResourceTypeRepositoryAdapter(ResourceTypeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ResourceType save(ResourceType resourceType) {
        jpaRepository.save(new ResourceTypeJpaEntity(resourceType.getId(), resourceType.getProviderId(),
                resourceType.getName(), resourceType.getDescription()));
        return resourceType;
    }

    @Override
    public Optional<ResourceType> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> ResourceType.rehydrate(e.getId(), e.getProviderId(),
                e.getName(), e.getDescription()));
    }

    @Override
    public boolean existsByProviderIdAndName(UUID providerId, String name) {
        return jpaRepository.existsByProviderIdAndName(providerId, name);
    }
}
