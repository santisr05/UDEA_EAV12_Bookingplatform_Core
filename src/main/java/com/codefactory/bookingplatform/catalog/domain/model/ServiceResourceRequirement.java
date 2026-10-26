package com.codefactory.bookingplatform.catalog.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.util.UUID;

public record ServiceResourceRequirement(UUID id, UUID serviceId, UUID resourceTypeId, int quantityRequired) {
    public ServiceResourceRequirement {
        if (quantityRequired <= 0) {
            throw BusinessException.rule("La cantidad requerida de un recurso debe ser mayor a cero");
        }
    }
}
