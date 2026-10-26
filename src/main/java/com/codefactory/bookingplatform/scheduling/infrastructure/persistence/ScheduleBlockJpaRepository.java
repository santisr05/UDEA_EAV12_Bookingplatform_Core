package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduleBlockJpaRepository extends JpaRepository<ScheduleBlockJpaEntity, UUID> {
    List<ScheduleBlockJpaEntity> findByScheduleId(UUID scheduleId);
}
