package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceResourceRequirementJpaRepository extends JpaRepository<ServiceResourceRequirementJpaEntity, UUID> {
    List<ServiceResourceRequirementJpaEntity> findByServiceId(UUID serviceId);
}
