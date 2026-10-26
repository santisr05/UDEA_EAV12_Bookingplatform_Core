package com.codefactory.bookingplatform.auth.domain.model;

import java.util.UUID;

public record UserRoleAssignment(UUID userId, UUID roleId) {
}
