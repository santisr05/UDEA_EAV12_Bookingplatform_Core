package com.codefactory.bookingplatform.catalog.application;

import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import com.codefactory.bookingplatform.catalog.domain.port.ResourceInstanceAvailabilityQuery;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceOfferingRepository;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceResourceRequirementRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ToggleServiceStatusUseCase {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceResourceRequirementRepository requirementRepository;
    private final ResourceInstanceAvailabilityQuery resourceInstanceAvailabilityQuery;

    public ToggleServiceStatusUseCase(ServiceOfferingRepository serviceOfferingRepository,
                                       ServiceResourceRequirementRepository requirementRepository,
                                       ResourceInstanceAvailabilityQuery resourceInstanceAvailabilityQuery) {
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.requirementRepository = requirementRepository;
        this.resourceInstanceAvailabilityQuery = resourceInstanceAvailabilityQuery;
    }

    @Transactional
    public void activate(UUID serviceId) {
        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.notFound("Servicio no encontrado"));

        boolean allResourceTypesHaveInstances = requirementRepository.findByServiceId(serviceId).stream()
                .allMatch(req -> resourceInstanceAvailabilityQuery.hasAvailableInstance(req.resourceTypeId()));
        if (!allResourceTypesHaveInstances) {
            throw BusinessException.rule(
                    "No se puede activar el servicio: falta registrar instancias disponibles de alguno de sus tipos de recurso");
        }

        service.activate();
        serviceOfferingRepository.save(service);
    }

    @Transactional
    public void deactivate(UUID serviceId) {
        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.notFound("Servicio no encontrado"));
        // HU-004, escenario "Desactivar un servicio con reservas": nunca se
        // elimina, solo se desactiva, incluso si tiene reservas asociadas.
        service.deactivate();
        serviceOfferingRepository.save(service);
    }
}
