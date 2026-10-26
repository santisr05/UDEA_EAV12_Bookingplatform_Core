package com.codefactory.bookingplatform.auth.infrastructure.rbac;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, UUID> {
    Optional<UserRoleJpaEntity> findByUserId(UUID userId);
    long countByRoleId(UUID roleId);
}
