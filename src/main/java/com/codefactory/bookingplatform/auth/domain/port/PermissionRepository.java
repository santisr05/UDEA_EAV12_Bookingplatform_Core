package com.codefactory.bookingplatform.auth.domain.port;

import com.codefactory.bookingplatform.auth.domain.model.Permission;

import java.util.List;
import java.util.UUID;

public interface PermissionRepository {
    List<Permission> findByRoleId(UUID roleId);
}
