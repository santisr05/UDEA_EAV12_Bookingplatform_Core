package com.codefactory.bookingplatform.booking.domain.port;

import com.codefactory.bookingplatform.booking.domain.model.Booking;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(UUID id);
    boolean existsOverlapForClient(UUID clientId, Instant startsAt, Instant endsAt);
    long countNoShowsSince(UUID clientId, Instant since);
}
