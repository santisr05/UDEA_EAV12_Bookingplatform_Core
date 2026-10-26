package com.codefactory.bookingplatform.auth.application;

import com.codefactory.bookingplatform.auth.domain.model.Role;
import com.codefactory.bookingplatform.auth.domain.model.UserRoleAssignment;
import com.codefactory.bookingplatform.auth.domain.port.RoleRepository;
import com.codefactory.bookingplatform.auth.domain.port.UserRoleRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * HU-022. Como las autoridades del usuario se recalculan desde esta tabla en
 * cada peticion (ver JwtAuthoritiesResolver), un cambio de rol aplica desde
 * la siguiente peticion sin necesidad de invalidar el JWT en Supabase.
 */
@Service
public class AssignRoleUseCase {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public AssignRoleUseCase(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public void assign(UUID userId, String roleName) {
        Role targetRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> BusinessException.notFound("El rol " + roleName + " no existe"));

        userRoleRepository.findByUserId(userId).ifPresent(current -> {
            if (!current.roleId().equals(targetRole.id())) {
                Role currentRole = roleRepository.findById(current.roleId()).orElse(null);
                if (currentRole != null && Role.ADMIN.equals(currentRole.name())
                        && userRoleRepository.countUsersWithRole(currentRole.id()) <= 1) {
                    throw BusinessException.rule("No se puede quitar el rol a el ultimo administrador de la plataforma");
                }
            }
        });

        userRoleRepository.upsert(new UserRoleAssignment(userId, targetRole.id()));
    }
}
