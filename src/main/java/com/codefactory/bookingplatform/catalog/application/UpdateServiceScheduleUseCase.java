package com.codefactory.bookingplatform.catalog.application;

import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceOfferingRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateServiceScheduleUseCase {

    private final ServiceOfferingRepository serviceOfferingRepository;

    public UpdateServiceScheduleUseCase(ServiceOfferingRepository serviceOfferingRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    @Transactional
    public void update(UUID serviceId, int durationMinutes, int preparationMinutes, int cleanupMinutes) {
        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.notFound("Servicio no encontrado"));
        service.updateSchedule(durationMinutes, preparationMinutes, cleanupMinutes);
        serviceOfferingRepository.save(service);
    }
}
