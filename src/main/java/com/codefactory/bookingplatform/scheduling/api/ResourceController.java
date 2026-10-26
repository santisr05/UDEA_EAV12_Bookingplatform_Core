package com.codefactory.bookingplatform.scheduling.api;

import com.codefactory.bookingplatform.scheduling.application.CreateResourceUseCase;
import com.codefactory.bookingplatform.scheduling.application.RetireResourceUseCase;
import com.codefactory.bookingplatform.scheduling.domain.model.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resources")
@PreAuthorize("hasAnyRole('PROVEEDOR', 'ADMINISTRADOR')")
public class ResourceController {

    private final CreateResourceUseCase createResourceUseCase;
    private final RetireResourceUseCase retireResourceUseCase;

    public ResourceController(CreateResourceUseCase createResourceUseCase, RetireResourceUseCase retireResourceUseCase) {
        this.createResourceUseCase = createResourceUseCase;
        this.retireResourceUseCase = retireResourceUseCase;
    }

    public record CreateRequest(@NotNull UUID resourceTypeId, @NotNull UUID locationId, @NotBlank String name,
                                 @Min(1) int capacity) {}
    public record ResourceResponse(UUID resourceId, String status) {}

    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody CreateRequest request) {
        Resource resource = createResourceUseCase.create(request.resourceTypeId(), request.locationId(),
                request.name(), request.capacity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResourceResponse(resource.getId(), resource.getStatus().name()));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> retire(@PathVariable UUID resourceId) {
        retireResourceUseCase.retire(resourceId);
        return ResponseEntity.noContent().build();
    }
}
