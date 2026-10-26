package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResourceJpaRepository extends JpaRepository<ResourceJpaEntity, UUID> {
    List<ResourceJpaEntity> findByResourceTypeId(UUID resourceTypeId);
    boolean existsByResourceTypeIdAndStatus(UUID resourceTypeId, String status);
}
