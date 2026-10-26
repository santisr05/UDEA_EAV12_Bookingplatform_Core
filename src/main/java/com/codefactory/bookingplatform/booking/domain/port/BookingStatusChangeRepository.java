package com.codefactory.bookingplatform.booking.domain.port;

import com.codefactory.bookingplatform.booking.domain.model.BookingStatusChange;

public interface BookingStatusChangeRepository {
    BookingStatusChange save(BookingStatusChange change);
}
