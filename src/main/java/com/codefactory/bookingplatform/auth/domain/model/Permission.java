package com.codefactory.bookingplatform.auth.domain.model;

import java.util.UUID;

public record Permission(UUID id, String code, String description) {
}
