package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import com.codefactory.bookingplatform.scheduling.domain.model.Resource;
import com.codefactory.bookingplatform.scheduling.domain.model.ResourceStatus;
import com.codefactory.bookingplatform.scheduling.domain.port.ResourceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ResourceRepositoryAdapter implements ResourceRepository {

    private final ResourceJpaRepository jpaRepository;

    public ResourceRepositoryAdapter(ResourceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Resource save(Resource resource) {
        jpaRepository.save(new ResourceJpaEntity(resource.getId(), resource.getResourceTypeId(),
                resource.getLocationId(), resource.getName(), resource.getCapacity(), resource.getStatus().name()));
        return resource;
    }

    @Override
    public Optional<Resource> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Resource> findByResourceTypeId(UUID resourceTypeId) {
        return jpaRepository.findByResourceTypeId(resourceTypeId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsAvailableByResourceTypeId(UUID resourceTypeId) {
        return jpaRepository.existsByResourceTypeIdAndStatus(resourceTypeId, ResourceStatus.AVAILABLE.name());
    }

    private Resource toDomain(ResourceJpaEntity e) {
        return Resource.rehydrate(e.getId(), e.getResourceTypeId(), e.getLocationId(), e.getName(), e.getCapacity(),
                ResourceStatus.valueOf(e.getStatus()));
    }
}
