package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProviderStaffJpaRepository extends JpaRepository<ProviderStaffJpaEntity, UUID> {
    boolean existsByUserIdAndProviderId(UUID userId, UUID providerId);
}
