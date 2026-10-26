package com.codefactory.bookingplatform.identity.domain.model;

import java.util.UUID;

public record AdminUser(UUID id, UUID userId, boolean mfaEnabled) {
}
