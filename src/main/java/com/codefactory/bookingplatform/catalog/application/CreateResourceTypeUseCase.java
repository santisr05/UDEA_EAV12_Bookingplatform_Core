package com.codefactory.bookingplatform.catalog.application;

import com.codefactory.bookingplatform.catalog.domain.model.ResourceType;
import com.codefactory.bookingplatform.catalog.domain.port.ResourceTypeRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * HU-009 (parte de catalogo: el tipo de recurso). Las instancias concretas
 * se crean en el modulo scheduling (CreateResourceUseCase), que es quien las
 * usa para calcular disponibilidad y asignarlas a reservas.
 */
@Service
public class CreateResourceTypeUseCase {

    private final ResourceTypeRepository resourceTypeRepository;

    public CreateResourceTypeUseCase(ResourceTypeRepository resourceTypeRepository) {
        this.resourceTypeRepository = resourceTypeRepository;
    }

    @Transactional
    public ResourceType create(UUID providerId, String name, String description) {
        if (resourceTypeRepository.existsByProviderIdAndName(providerId, name)) {
            throw BusinessException.duplicate("Ya existe un tipo de recurso con ese nombre");
        }
        ResourceType resourceType = ResourceType.create(UUID.randomUUID(), providerId, name, description);
        return resourceTypeRepository.save(resourceType);
    }
}
