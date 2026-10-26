package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationJpaRepository extends JpaRepository<LocationJpaEntity, UUID> {
    List<LocationJpaEntity> findByProviderId(UUID providerId);
}
