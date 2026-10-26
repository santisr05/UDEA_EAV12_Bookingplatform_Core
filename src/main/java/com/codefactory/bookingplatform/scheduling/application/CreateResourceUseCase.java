package com.codefactory.bookingplatform.scheduling.application;

import com.codefactory.bookingplatform.scheduling.domain.model.Resource;
import com.codefactory.bookingplatform.scheduling.domain.port.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** HU-009, parte 2: instancias concretas de un tipo de recurso. */
@Service
public class CreateResourceUseCase {

    private final ResourceRepository resourceRepository;

    public CreateResourceUseCase(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public Resource create(UUID resourceTypeId, UUID locationId, String name, int capacity) {
        Resource resource = Resource.create(UUID.randomUUID(), resourceTypeId, locationId, name, capacity);
        return resourceRepository.save(resource);
    }
}
