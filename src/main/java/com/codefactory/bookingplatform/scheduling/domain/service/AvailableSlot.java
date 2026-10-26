package com.codefactory.bookingplatform.scheduling.domain.service;

import java.time.Instant;

public record AvailableSlot(Instant startsAt, Instant endsAt) {
}
