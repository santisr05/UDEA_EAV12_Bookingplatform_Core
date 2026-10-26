package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import com.codefactory.bookingplatform.catalog.domain.port.NextAvailableSlotQuery;
import com.codefactory.bookingplatform.catalog.domain.port.ResourceInstanceAvailabilityQuery;
import com.codefactory.bookingplatform.scheduling.domain.port.ResourceRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementa, desde scheduling, los puertos que catalog define para no
 * depender de scheduling en tiempo de compilacion (inversion de dependencia).
 */
@Component
public class CatalogQueryAdapter implements ResourceInstanceAvailabilityQuery, NextAvailableSlotQuery {

    private final ResourceRepository resourceRepository;

    public CatalogQueryAdapter(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public boolean hasAvailableInstance(UUID resourceTypeId) {
        return resourceRepository.existsAvailableByResourceTypeId(resourceTypeId);
    }

    @Override
    public Optional<Instant> nextAvailableSlot(UUID serviceId) {
        // Implementacion simplificada: calcular la proxima franja real exige
        // resolver la agenda y los recursos exactos del servicio (join con
        // catalog.service_resource_requirement), lo cual queda fuera del
        // alcance de este corte inicial. Se deja el contrato del puerto listo
        // para completarse junto con el motor de busqueda del catalogo.
        return Optional.empty();
    }
}
