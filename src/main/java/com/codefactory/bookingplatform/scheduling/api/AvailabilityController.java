package com.codefactory.bookingplatform.scheduling.api;

import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceOfferingRepository;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceResourceRequirementRepository;
import com.codefactory.bookingplatform.identity.domain.model.Provider;
import com.codefactory.bookingplatform.identity.domain.port.ProviderRepository;
import com.codefactory.bookingplatform.scheduling.application.GetAvailabilityUseCase;
import com.codefactory.bookingplatform.scheduling.domain.service.AvailableSlot;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

/**
 * HU-008 / HU-011. Orquesta la informacion de catalogo (duracion del
 * servicio, recursos requeridos) e identidad (zona horaria y anticipacion
 * del proveedor) para alimentar el calculo puro de scheduling. Esta
 * composicion vive aqui, en la capa api, porque es el "pegamento" entre
 * modulos de un monolito modular - el dominio de cada modulo se mantiene
 * aislado.
 */
@RestController
@RequestMapping("/api/v1/services/{serviceId}/availability")
public class AvailabilityController {

    private final GetAvailabilityUseCase getAvailabilityUseCase;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceResourceRequirementRepository requirementRepository;
    private final ProviderRepository providerRepository;

    public AvailabilityController(GetAvailabilityUseCase getAvailabilityUseCase,
                                   ServiceOfferingRepository serviceOfferingRepository,
                                   ServiceResourceRequirementRepository requirementRepository,
                                   ProviderRepository providerRepository) {
        this.getAvailabilityUseCase = getAvailabilityUseCase;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.requirementRepository = requirementRepository;
        this.providerRepository = providerRepository;
    }

    @GetMapping
    public List<AvailableSlot> getAvailability(@PathVariable UUID serviceId,
                                                @RequestParam UUID scheduleId,
                                                @RequestParam LocalDate from,
                                                @RequestParam(required = false) LocalDate to) {
        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.notFound("Servicio no encontrado"));
        Provider provider = providerRepository.findById(service.getProviderId())
                .orElseThrow(() -> BusinessException.notFound("Proveedor no encontrado"));

        List<GetAvailabilityUseCase.RequiredResourceType> required = requirementRepository.findByServiceId(serviceId).stream()
                .map(r -> new GetAvailabilityUseCase.RequiredResourceType(r.resourceTypeId(), r.quantityRequired()))
                .toList();

        LocalDate rangeEnd = to != null ? to : from.plusDays(30);
        int totalDuration = service.getDurationMinutes() + service.getPreparationMinutes() + service.getCleanupMinutes();

        return getAvailabilityUseCase.getAvailability(scheduleId, totalDuration, required,
                provider.getMinBookingNoticeMinutes(), provider.getMaxBookingNoticeMinutes(), from, rangeEnd,
                ZoneId.of(provider.getTimezone()));
    }
}
