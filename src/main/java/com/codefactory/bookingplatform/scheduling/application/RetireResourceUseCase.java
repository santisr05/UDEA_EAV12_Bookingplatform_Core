package com.codefactory.bookingplatform.scheduling.application;

import com.codefactory.bookingplatform.scheduling.domain.model.Resource;
import com.codefactory.bookingplatform.scheduling.domain.port.FutureBookingsQuery;
import com.codefactory.bookingplatform.scheduling.domain.port.ResourceRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RetireResourceUseCase {

    private final ResourceRepository resourceRepository;
    private final FutureBookingsQuery futureBookingsQuery;

    public RetireResourceUseCase(ResourceRepository resourceRepository, FutureBookingsQuery futureBookingsQuery) {
        this.resourceRepository = resourceRepository;
        this.futureBookingsQuery = futureBookingsQuery;
    }

    @Transactional
    public void retire(UUID resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> BusinessException.notFound("Recurso no encontrado"));
        if (futureBookingsQuery.countFutureConfirmedBookingsForResource(resourceId) > 0) {
            throw BusinessException.invalidState(
                    "No se puede retirar el recurso: tiene reservas futuras confirmadas pendientes de resolver");
        }
        resource.retire();
        resourceRepository.save(resource);
    }
}
