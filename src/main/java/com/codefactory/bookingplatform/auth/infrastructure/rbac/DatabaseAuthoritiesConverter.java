package com.codefactory.bookingplatform.auth.infrastructure.rbac;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * HU-022: en lugar de leer el rol desde un claim fijo del JWT, lo resuelve en
 * cada peticion contra nuestra propia tabla user_role/role_permission. Esto
 * es lo que hace que un cambio de rol hecho por un administrador aplique
 * "de inmediato": la siguiente peticion ya ve las autoridades nuevas, sin
 * depender de revocar o reemitir el token en Supabase.
 */
@Component
public class DatabaseAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    public DatabaseAuthoritiesConverter(UserRoleJpaRepository userRoleJpaRepository,
                                         RoleJpaRepository roleJpaRepository,
                                         PermissionJpaRepository permissionJpaRepository) {
        this.userRoleJpaRepository = userRoleJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        UUID userId;
        try {
            userId = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            return authorities;
        }
        userRoleJpaRepository.findByUserId(userId).ifPresent(assignment -> {
            roleJpaRepository.findById(assignment.getRoleId()).ifPresent(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
            permissionJpaRepository.findByRoleId(assignment.getRoleId())
                    .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getCode())));
        });
        return authorities;
    }
}
