package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScheduleJpaRepository extends JpaRepository<ScheduleJpaEntity, UUID> {
}
