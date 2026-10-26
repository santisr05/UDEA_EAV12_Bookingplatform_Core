package com.codefactory.bookingplatform.catalog.domain.port;

import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;

import java.util.Optional;
import java.util.UUID;

public interface ServiceOfferingRepository {
    ServiceOffering save(ServiceOffering service);
    Optional<ServiceOffering> findById(UUID id);
    boolean existsByProviderIdAndName(UUID providerId, String name);
    boolean hasBookings(UUID serviceId);
}
