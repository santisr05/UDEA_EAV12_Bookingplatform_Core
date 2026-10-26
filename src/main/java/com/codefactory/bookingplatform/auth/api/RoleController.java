package com.codefactory.bookingplatform.auth.api;

import com.codefactory.bookingplatform.auth.application.AssignRoleUseCase;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * HU-022: solo un administrador de la plataforma puede reasignar roles.
 */
@RestController
@RequestMapping("/api/v1/admin/users/{userId}/role")
public class RoleController {

    private final AssignRoleUseCase assignRoleUseCase;

    public RoleController(AssignRoleUseCase assignRoleUseCase) {
        this.assignRoleUseCase = assignRoleUseCase;
    }

    public record AssignRoleRequest(@NotBlank String role) {}

    @PutMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> assignRole(@PathVariable UUID userId,
                                            @org.springframework.validation.annotation.Validated @org.springframework.web.bind.annotation.RequestBody AssignRoleRequest request) {
        assignRoleUseCase.assign(userId, request.role());
        return ResponseEntity.noContent().build();
    }
}
