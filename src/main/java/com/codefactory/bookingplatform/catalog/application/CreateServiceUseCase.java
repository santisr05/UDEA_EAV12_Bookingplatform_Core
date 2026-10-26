package com.codefactory.bookingplatform.catalog.application;

import com.codefactory.bookingplatform.catalog.domain.model.CapacityType;
import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import com.codefactory.bookingplatform.catalog.domain.model.ServiceResourceRequirement;
import com.codefactory.bookingplatform.catalog.domain.port.CurrencyRepository;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceOfferingRepository;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceResourceRequirementRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CreateServiceUseCase {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceResourceRequirementRepository requirementRepository;
    private final CurrencyRepository currencyRepository;

    public CreateServiceUseCase(ServiceOfferingRepository serviceOfferingRepository,
                                 ServiceResourceRequirementRepository requirementRepository,
                                 CurrencyRepository currencyRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.requirementRepository = requirementRepository;
        this.currencyRepository = currencyRepository;
    }

    public record RequiredResource(UUID resourceTypeId, int quantity) {}

    @Transactional
    public ServiceOffering create(UUID providerId, String name, String description, int durationMinutes,
                                   int preparationMinutes, int cleanupMinutes, BigDecimal price, String currencyCode,
                                   CapacityType capacityType, int maxCapacity, List<RequiredResource> requiredResources) {
        if (serviceOfferingRepository.existsByProviderIdAndName(providerId, name)) {
            throw BusinessException.duplicate("Ya existe un servicio con ese nombre");
        }
        if (requiredResources == null || requiredResources.isEmpty()) {
            throw BusinessException.rule("Un servicio debe declarar al menos un tipo de recurso requerido");
        }
        UUID currencyId = currencyRepository.findByCode(currencyCode)
                .orElseThrow(() -> BusinessException.notFound("Moneda no soportada: " + currencyCode))
                .id();

        ServiceOffering service = ServiceOffering.create(UUID.randomUUID(), providerId, name, description,
                durationMinutes, preparationMinutes, cleanupMinutes, price, currencyId, capacityType, maxCapacity);
        service = serviceOfferingRepository.save(service);

        for (RequiredResource required : requiredResources) {
            requirementRepository.save(new ServiceResourceRequirement(UUID.randomUUID(), service.getId(),
                    required.resourceTypeId(), required.quantity()));
        }
        return service;
    }
}
