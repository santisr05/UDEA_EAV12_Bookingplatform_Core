package com.codefactory.bookingplatform.auth.domain.port;

import com.codefactory.bookingplatform.auth.domain.model.UserRoleAssignment;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository {
    UserRoleAssignment upsert(UserRoleAssignment assignment);
    Optional<UserRoleAssignment> findByUserId(UUID userId);
    long countUsersWithRole(UUID roleId);
}
