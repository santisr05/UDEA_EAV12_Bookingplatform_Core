package com.codefactory.bookingplatform.auth.infrastructure.rbac;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_role")
public class UserRoleJpaEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    protected UserRoleJpaEntity() {
    }

    public UserRoleJpaEntity(UUID userId, UUID roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UUID getUserId() { return userId; }
    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
}
