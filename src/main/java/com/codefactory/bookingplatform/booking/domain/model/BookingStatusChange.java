package com.codefactory.bookingplatform.booking.domain.model;

import java.time.Instant;
import java.util.UUID;

public record BookingStatusChange(UUID id, UUID bookingId, BookingStatus fromStatus, BookingStatus toStatus,
                                   String reason, UUID changedBy, Instant changedAt) {
}
