package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceOfferingJpaRepository extends JpaRepository<ServiceOfferingJpaEntity, UUID> {
    boolean existsByProviderIdAndName(UUID providerId, String name);
}
