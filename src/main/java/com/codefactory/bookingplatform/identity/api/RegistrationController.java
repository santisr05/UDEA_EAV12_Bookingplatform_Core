package com.codefactory.bookingplatform.identity.api;

import com.codefactory.bookingplatform.identity.application.ConfirmClientEmailUseCase;
import com.codefactory.bookingplatform.identity.application.RegisterClientUseCase;
import com.codefactory.bookingplatform.identity.application.ResendVerificationUseCase;
import com.codefactory.bookingplatform.identity.application.dto.RegisterClientCommand;
import com.codefactory.bookingplatform.identity.domain.model.Client;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registrations")
public class RegistrationController {

    private final RegisterClientUseCase registerClientUseCase;
    private final ConfirmClientEmailUseCase confirmClientEmailUseCase;
    private final ResendVerificationUseCase resendVerificationUseCase;

    public RegistrationController(RegisterClientUseCase registerClientUseCase,
                                   ConfirmClientEmailUseCase confirmClientEmailUseCase,
                                   ResendVerificationUseCase resendVerificationUseCase) {
        this.registerClientUseCase = registerClientUseCase;
        this.confirmClientEmailUseCase = confirmClientEmailUseCase;
        this.resendVerificationUseCase = resendVerificationUseCase;
    }

    public record RegisterRequest(
            @NotBlank String fullName,
            @NotBlank String document,
            @Past LocalDate birthDate,
            @NotBlank @Email String email,
            @NotBlank String password,
            String phone,
            String city,
            String notificationChannel) {}

    public record RegisterResponse(UUID clientId, String status) {}
    public record ConfirmEmailRequest(@NotBlank UUID clientId, @NotBlank @Email String email, @NotBlank String otpToken) {}
    public record ResendRequest(@NotBlank @Email String email) {}

    @PostMapping
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        Client client = registerClientUseCase.register(new RegisterClientCommand(request.fullName(),
                request.document(), request.birthDate(), request.email(), request.password(), request.phone(),
                request.city(), request.notificationChannel()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(client.getId(), client.getStatus().name()));
    }

    @PostMapping("/email-verifications")
    public ResponseEntity<Void> confirmEmail(@Valid @RequestBody ConfirmEmailRequest request) {
        confirmClientEmailUseCase.confirm(request.clientId(), request.email(), request.otpToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verification-resends")
    public ResponseEntity<Void> resend(@Valid @RequestBody ResendRequest request) {
        resendVerificationUseCase.resend(request.email());
        return ResponseEntity.accepted().build();
    }
}
