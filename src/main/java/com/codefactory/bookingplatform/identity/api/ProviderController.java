package com.codefactory.bookingplatform.identity.api;

import com.codefactory.bookingplatform.identity.application.ApproveProviderUseCase;
import com.codefactory.bookingplatform.identity.application.RegisterProviderUseCase;
import com.codefactory.bookingplatform.identity.application.UpdateOperationPolicyUseCase;
import com.codefactory.bookingplatform.identity.application.dto.RegisterProviderCommand;
import com.codefactory.bookingplatform.identity.domain.model.Provider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {

    private final RegisterProviderUseCase registerProviderUseCase;
    private final ApproveProviderUseCase approveProviderUseCase;
    private final UpdateOperationPolicyUseCase updateOperationPolicyUseCase;

    public ProviderController(RegisterProviderUseCase registerProviderUseCase,
                               ApproveProviderUseCase approveProviderUseCase,
                               UpdateOperationPolicyUseCase updateOperationPolicyUseCase) {
        this.registerProviderUseCase = registerProviderUseCase;
        this.approveProviderUseCase = approveProviderUseCase;
        this.updateOperationPolicyUseCase = updateOperationPolicyUseCase;
    }

    public record RegisterRequest(
            @NotBlank String legalName,
            @NotBlank String nit,
            String category,
            @NotBlank @Email String contactEmail,
            String contactPhone,
            @NotBlank String timezone,
            @NotBlank String password,
            @Min(0) int minBookingNoticeMinutes,
            @Min(0) int maxBookingNoticeMinutes,
            @Min(0) int freeCancellationWindowMinutes,
            @NotBlank String firstLocationName,
            String firstLocationAddress,
            String firstLocationCity) {}

    public record ProviderResponse(UUID providerId, String status) {}
    public record OperationPolicyRequest(@Min(0) int minBookingNoticeMinutes, @Min(0) int maxBookingNoticeMinutes,
                                          @Min(0) int freeCancellationWindowMinutes) {}

    @PostMapping
    public ResponseEntity<ProviderResponse> register(@Valid @RequestBody RegisterRequest request) {
        Provider provider = registerProviderUseCase.register(new RegisterProviderCommand(request.legalName(),
                request.nit(), request.category(), request.contactEmail(), request.contactPhone(),
                request.timezone(), request.password(), request.minBookingNoticeMinutes(),
                request.maxBookingNoticeMinutes(), request.freeCancellationWindowMinutes(),
                request.firstLocationName(), request.firstLocationAddress(), request.firstLocationCity()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ProviderResponse(provider.getId(), provider.getStatus().name()));
    }

    @PostMapping("/{providerId}/approval")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> approve(@PathVariable UUID providerId) {
        approveProviderUseCase.approve(providerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{providerId}/operation-policy")
    @PreAuthorize("hasAnyRole('PROVEEDOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> updateOperationPolicy(@PathVariable UUID providerId,
                                                       @Valid @RequestBody OperationPolicyRequest request) {
        updateOperationPolicyUseCase.update(providerId, request.minBookingNoticeMinutes(),
                request.maxBookingNoticeMinutes(), request.freeCancellationWindowMinutes());
        return ResponseEntity.noContent().build();
    }
}
