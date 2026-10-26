package com.codefactory.bookingplatform.booking.domain.port;

import com.codefactory.bookingplatform.booking.domain.model.BookingConditionSnapshot;

import java.util.Optional;
import java.util.UUID;

public interface BookingConditionSnapshotRepository {
    BookingConditionSnapshot save(BookingConditionSnapshot snapshot);
    Optional<BookingConditionSnapshot> findByBookingId(UUID bookingId);
}
