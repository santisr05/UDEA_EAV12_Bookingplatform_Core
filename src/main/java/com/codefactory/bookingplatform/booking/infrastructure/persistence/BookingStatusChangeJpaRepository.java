package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingStatusChangeJpaRepository extends JpaRepository<BookingStatusChangeJpaEntity, UUID> {
}
