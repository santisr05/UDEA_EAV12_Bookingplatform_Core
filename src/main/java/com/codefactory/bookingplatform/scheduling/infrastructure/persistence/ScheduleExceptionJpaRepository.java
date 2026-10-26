package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ScheduleExceptionJpaRepository extends JpaRepository<ScheduleExceptionJpaEntity, UUID> {
    @Query("select e from ScheduleExceptionJpaEntity e where e.scheduleId = :scheduleId " +
           "and e.startsAt < :to and e.endsAt > :from")
    List<ScheduleExceptionJpaEntity> findByScheduleIdAndRange(@Param("scheduleId") UUID scheduleId,
                                                               @Param("from") Instant from, @Param("to") Instant to);
}
