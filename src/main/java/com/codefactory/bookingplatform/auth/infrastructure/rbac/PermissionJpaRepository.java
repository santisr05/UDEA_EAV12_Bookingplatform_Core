package com.codefactory.bookingplatform.auth.infrastructure.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, UUID> {

    @Query("select p from PermissionJpaEntity p where p.id in " +
           "(select rp.permissionId from RolePermissionJpaEntity rp where rp.roleId = :roleId)")
    List<PermissionJpaEntity> findByRoleId(@Param("roleId") UUID roleId);
}
