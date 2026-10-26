package com.codefactory.bookingplatform.auth.infrastructure.rbac;

import com.codefactory.bookingplatform.auth.domain.model.Permission;
import com.codefactory.bookingplatform.auth.domain.model.Role;
import com.codefactory.bookingplatform.auth.domain.model.UserRoleAssignment;
import com.codefactory.bookingplatform.auth.domain.port.PermissionRepository;
import com.codefactory.bookingplatform.auth.domain.port.RoleRepository;
import com.codefactory.bookingplatform.auth.domain.port.UserRoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RbacRepositoryAdapter implements RoleRepository, PermissionRepository, UserRoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;

    public RbacRepositoryAdapter(RoleJpaRepository roleJpaRepository,
                                  PermissionJpaRepository permissionJpaRepository,
                                  UserRoleJpaRepository userRoleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
        this.permissionJpaRepository = permissionJpaRepository;
        this.userRoleJpaRepository = userRoleJpaRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(e -> new Role(e.getId(), e.getName()));
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleJpaRepository.findById(id).map(e -> new Role(e.getId(), e.getName()));
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream().map(e -> new Role(e.getId(), e.getName())).toList();
    }

    @Override
    public Role save(Role role) {
        RoleJpaEntity entity = new RoleJpaEntity(role.name());
        return new Role(roleJpaRepository.save(entity).getId(), role.name());
    }

    @Override
    public List<Permission> findByRoleId(UUID roleId) {
        return permissionJpaRepository.findByRoleId(roleId).stream()
                .map(e -> new Permission(e.getId(), e.getCode(), e.getDescription()))
                .toList();
    }

    @Override
    public UserRoleAssignment upsert(UserRoleAssignment assignment) {
        UserRoleJpaEntity entity = userRoleJpaRepository.findByUserId(assignment.userId())
                .orElse(new UserRoleJpaEntity(assignment.userId(), assignment.roleId()));
        entity.setRoleId(assignment.roleId());
        userRoleJpaRepository.save(entity);
        return assignment;
    }

    @Override
    public Optional<UserRoleAssignment> findByUserId(UUID userId) {
        return userRoleJpaRepository.findByUserId(userId)
                .map(e -> new UserRoleAssignment(e.getUserId(), e.getRoleId()));
    }

    @Override
    public long countUsersWithRole(UUID roleId) {
        return userRoleJpaRepository.countByRoleId(roleId);
    }
}
