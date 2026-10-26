package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import com.codefactory.bookingplatform.catalog.domain.model.CapacityType;
import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import com.codefactory.bookingplatform.catalog.domain.model.ServiceStatus;
import com.codefactory.bookingplatform.catalog.domain.port.BookingLookupForCatalog;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceOfferingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ServiceOfferingRepositoryAdapter implements ServiceOfferingRepository {

    private final ServiceOfferingJpaRepository jpaRepository;
    private final BookingLookupForCatalog bookingLookupForCatalog;

    public ServiceOfferingRepositoryAdapter(ServiceOfferingJpaRepository jpaRepository,
                                             BookingLookupForCatalog bookingLookupForCatalog) {
        this.jpaRepository = jpaRepository;
        this.bookingLookupForCatalog = bookingLookupForCatalog;
    }

    @Override
    public ServiceOffering save(ServiceOffering service) {
        jpaRepository.save(new ServiceOfferingJpaEntity(service.getId(), service.getProviderId(), service.getName(),
                service.getDescription(), service.getDurationMinutes(), service.getPreparationMinutes(),
                service.getCleanupMinutes(), service.getPrice(), service.getCurrencyId(),
                service.getCapacityType().name(), service.getMaxCapacity(), service.getStatus().name()));
        return service;
    }

    @Override
    public Optional<ServiceOffering> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> ServiceOffering.rehydrate(e.getId(), e.getProviderId(),
                e.getName(), e.getDescription(), e.getDurationMinutes(), e.getPreparationMinutes(),
                e.getCleanupMinutes(), e.getPrice(), e.getCurrencyId(), CapacityType.valueOf(e.getCapacityType()),
                e.getMaxCapacity(), ServiceStatus.valueOf(e.getStatus())));
    }

    @Override
    public boolean existsByProviderIdAndName(UUID providerId, String name) {
        return jpaRepository.existsByProviderIdAndName(providerId, name);
    }

    @Override
    public boolean hasBookings(UUID serviceId) {
        return bookingLookupForCatalog.hasAnyBooking(serviceId);
    }
}
