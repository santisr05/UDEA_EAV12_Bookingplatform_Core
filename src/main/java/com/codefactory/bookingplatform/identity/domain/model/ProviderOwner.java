package com.codefactory.bookingplatform.identity.domain.model;

import java.util.UUID;

public record ProviderOwner(UUID id, UUID userId, UUID providerId) {
}
