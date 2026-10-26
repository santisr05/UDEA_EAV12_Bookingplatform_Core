package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ProviderJpaRepository extends JpaRepository<ProviderJpaEntity, UUID>,
        JpaSpecificationExecutor<ProviderJpaEntity> {
    boolean existsByNit(String nit);
}
