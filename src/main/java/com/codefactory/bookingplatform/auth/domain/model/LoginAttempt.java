package com.codefactory.bookingplatform.auth.domain.model;

import java.time.Instant;

public record LoginAttempt(String email, boolean successful, Instant occurredAt) {
}
