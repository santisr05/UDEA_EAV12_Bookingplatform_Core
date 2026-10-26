package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookingJpaRepository extends JpaRepository<BookingJpaEntity, UUID> {

    @Query("select case when count(b) > 0 then true else false end from BookingJpaEntity b " +
           "where b.clientId = :clientId and b.status in ('PENDING','CONFIRMED') " +
           "and b.startsAt < :endsAt and b.endsAt > :startsAt")
    boolean existsOverlapForClient(@Param("clientId") UUID clientId, @Param("startsAt") Instant startsAt,
                                    @Param("endsAt") Instant endsAt);

    @Query("select count(b) from BookingJpaEntity b where b.clientId = :clientId and b.status = 'NO_SHOW' " +
           "and b.startsAt >= :since")
    long countNoShowsSince(@Param("clientId") UUID clientId, @Param("since") Instant since);

    @Query(value = "select count(*) from booking b join catalog_service s on s.id = b.service_id " +
           "where s.provider_id = :providerId and b.status = 'CONFIRMED' and b.starts_at > :now", nativeQuery = true)
    long countFutureConfirmedByProvider(@Param("providerId") UUID providerId, @Param("now") Instant now);

    @Query("select count(b) > 0 from BookingJpaEntity b where b.serviceId = :serviceId")
    boolean existsByServiceId(@Param("serviceId") UUID serviceId);

    List<BookingJpaEntity> findByLocationId(UUID locationId);

    @Query(value = "select count(distinct b.id) from booking b join resource_slot_lock l on l.booking_id = b.id " +
           "where l.resource_id = :resourceId and b.status = 'CONFIRMED' and b.starts_at > :now", nativeQuery = true)
    long countFutureConfirmedByResource(@Param("resourceId") UUID resourceId, @Param("now") Instant now);

    /**
     * Simplificacion: se aproxima "reservas de esta agenda" por reservas de
     * la misma sede, ya que Booking no referencia directamente schedule_id
     * (la relacion real pasa por los recursos asignados).
     */
    @Query(value = "select count(*) from booking b where b.location_id = :locationId and b.status = 'CONFIRMED' " +
           "and b.starts_at > :now", nativeQuery = true)
    long countFutureConfirmedByLocation(@Param("locationId") UUID locationId, @Param("now") Instant now);

    @Query(value = "select count(*) from booking b where b.location_id = :locationId and b.status = 'CONFIRMED' " +
           "and b.starts_at < :to and b.ends_at > :from", nativeQuery = true)
    long countFutureConfirmedByLocationInRange(@Param("locationId") UUID locationId, @Param("from") Instant from,
                                                @Param("to") Instant to);
}
