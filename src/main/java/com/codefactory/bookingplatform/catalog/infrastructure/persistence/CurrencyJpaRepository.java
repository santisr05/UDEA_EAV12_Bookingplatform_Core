package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyJpaRepository extends JpaRepository<CurrencyJpaEntity, UUID> {
    Optional<CurrencyJpaEntity> findByCode(String code);
}
