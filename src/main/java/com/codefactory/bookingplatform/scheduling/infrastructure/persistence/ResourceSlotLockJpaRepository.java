package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ResourceSlotLockJpaRepository extends JpaRepository<ResourceSlotLockJpaEntity, UUID> {

    void deleteByBookingId(UUID bookingId);

    @Query("select count(l) > 0 from ResourceSlotLockJpaEntity l where l.resourceId = :resourceId " +
           "and l.slotStart >= :start and l.slotStart < :end")
    boolean existsByResourceIdAndSlotStartBetween(@Param("resourceId") UUID resourceId,
                                                   @Param("start") Instant start, @Param("end") Instant end);
}
