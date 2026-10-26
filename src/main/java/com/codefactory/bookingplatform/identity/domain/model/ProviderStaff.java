package com.codefactory.bookingplatform.identity.domain.model;

import java.util.UUID;

public record ProviderStaff(UUID id, UUID userId, UUID providerId, String role) {
}
