package com.codefactory.bookingplatform.catalog.api;

import com.codefactory.bookingplatform.catalog.application.CreateServiceUseCase;
import com.codefactory.bookingplatform.catalog.application.ToggleServiceStatusUseCase;
import com.codefactory.bookingplatform.catalog.application.UpdateServiceScheduleUseCase;
import com.codefactory.bookingplatform.catalog.domain.model.CapacityType;
import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers/{providerId}/services")
@PreAuthorize("hasAnyRole('PROVEEDOR', 'ADMINISTRADOR')")
public class ServiceController {

    private final CreateServiceUseCase createServiceUseCase;
    private final UpdateServiceScheduleUseCase updateServiceScheduleUseCase;
    private final ToggleServiceStatusUseCase toggleServiceStatusUseCase;

    public ServiceController(CreateServiceUseCase createServiceUseCase,
                              UpdateServiceScheduleUseCase updateServiceScheduleUseCase,
                              ToggleServiceStatusUseCase toggleServiceStatusUseCase) {
        this.createServiceUseCase = createServiceUseCase;
        this.updateServiceScheduleUseCase = updateServiceScheduleUseCase;
        this.toggleServiceStatusUseCase = toggleServiceStatusUseCase;
    }

    public record RequiredResourceRequest(@NotNull UUID resourceTypeId, @Min(1) int quantity) {}

    public record CreateServiceRequest(
            @NotBlank String name,
            String description,
            @Min(1) int durationMinutes,
            @Min(0) int preparationMinutes,
            @Min(0) int cleanupMinutes,
            @NotNull BigDecimal price,
            @NotBlank String currencyCode,
            @NotNull CapacityType capacityType,
            @Min(1) int maxCapacity,
            @NotNull List<RequiredResourceRequest> requiredResources) {}

    public record ServiceResponse(UUID serviceId, String status) {}
    public record UpdateScheduleRequest(@Min(1) int durationMinutes, @Min(0) int preparationMinutes, @Min(0) int cleanupMinutes) {}

    @PostMapping
    public ResponseEntity<ServiceResponse> create(@PathVariable UUID providerId,
                                                   @Valid @RequestBody CreateServiceRequest request) {
        List<CreateServiceUseCase.RequiredResource> required = request.requiredResources().stream()
                .map(r -> new CreateServiceUseCase.RequiredResource(r.resourceTypeId(), r.quantity()))
                .toList();
        ServiceOffering service = createServiceUseCase.create(providerId, request.name(), request.description(),
                request.durationMinutes(), request.preparationMinutes(), request.cleanupMinutes(), request.price(),
                request.currencyCode(), request.capacityType(), request.maxCapacity(), required);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ServiceResponse(service.getId(), service.getStatus().name()));
    }

    @PutMapping("/{serviceId}/schedule")
    public ResponseEntity<Void> updateSchedule(@PathVariable UUID providerId, @PathVariable UUID serviceId,
                                                @Valid @RequestBody UpdateScheduleRequest request) {
        updateServiceScheduleUseCase.update(serviceId, request.durationMinutes(), request.preparationMinutes(),
                request.cleanupMinutes());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{serviceId}/activation")
    public ResponseEntity<Void> activate(@PathVariable UUID providerId, @PathVariable UUID serviceId) {
        toggleServiceStatusUseCase.activate(serviceId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID providerId, @PathVariable UUID serviceId) {
        toggleServiceStatusUseCase.deactivate(serviceId);
        return ResponseEntity.noContent().build();
    }
}
