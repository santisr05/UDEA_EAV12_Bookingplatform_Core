package com.codefactory.bookingplatform.catalog.api;

import com.codefactory.bookingplatform.catalog.application.CreateResourceTypeUseCase;
import com.codefactory.bookingplatform.catalog.domain.model.ResourceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers/{providerId}/resource-types")
@PreAuthorize("hasAnyRole('PROVEEDOR', 'ADMINISTRADOR')")
public class ResourceTypeController {

    private final CreateResourceTypeUseCase createResourceTypeUseCase;

    public ResourceTypeController(CreateResourceTypeUseCase createResourceTypeUseCase) {
        this.createResourceTypeUseCase = createResourceTypeUseCase;
    }

    public record CreateRequest(@NotBlank String name, String description) {}
    public record ResourceTypeResponse(UUID resourceTypeId, String name) {}

    @PostMapping
    public ResponseEntity<ResourceTypeResponse> create(@PathVariable UUID providerId,
                                                        @Valid @RequestBody CreateRequest request) {
        ResourceType resourceType = createResourceTypeUseCase.create(providerId, request.name(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResourceTypeResponse(resourceType.getId(), resourceType.getName()));
    }
}
