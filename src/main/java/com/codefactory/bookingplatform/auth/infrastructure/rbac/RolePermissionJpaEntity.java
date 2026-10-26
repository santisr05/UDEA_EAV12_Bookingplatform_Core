package com.codefactory.bookingplatform.auth.infrastructure.rbac;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "role_permission")
@IdClass(RolePermissionJpaEntity.Key.class)
public class RolePermissionJpaEntity {

    @Id
    @Column(name = "role_id")
    private UUID roleId;

    @Id
    @Column(name = "permission_id")
    private UUID permissionId;

    protected RolePermissionJpaEntity() {
    }

    public static class Key implements Serializable {
        private UUID roleId;
        private UUID permissionId;

        public Key() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key key)) return false;
            return Objects.equals(roleId, key.roleId) && Objects.equals(permissionId, key.permissionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(roleId, permissionId);
        }
    }
}
