package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, UUID>,
        JpaSpecificationExecutor<ClientJpaEntity> {
    boolean existsByDocument(String document);
    boolean existsByEmail(String email);
}
