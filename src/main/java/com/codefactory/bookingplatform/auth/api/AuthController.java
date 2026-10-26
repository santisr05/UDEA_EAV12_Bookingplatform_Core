package com.codefactory.bookingplatform.auth.api;

import com.codefactory.bookingplatform.auth.application.LoginUseCase;
import com.codefactory.bookingplatform.auth.application.LogoutUseCase;
import com.codefactory.bookingplatform.auth.application.PasswordRecoveryUseCase;
import com.codefactory.bookingplatform.auth.application.PasswordResetUseCase;
import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final PasswordRecoveryUseCase passwordRecoveryUseCase;
    private final PasswordResetUseCase passwordResetUseCase;

    public AuthController(LoginUseCase loginUseCase, LogoutUseCase logoutUseCase,
                           PasswordRecoveryUseCase passwordRecoveryUseCase,
                           PasswordResetUseCase passwordResetUseCase) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.passwordRecoveryUseCase = passwordRecoveryUseCase;
        this.passwordResetUseCase = passwordResetUseCase;
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record LoginResponse(String accessToken, String refreshToken) {}
    public record PasswordRecoveryRequest(@NotBlank @Email String email) {}
    public record PasswordResetRequest(@NotBlank String recoveryToken, @NotBlank String newPassword) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@org.springframework.validation.annotation.Validated @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        IdentityProviderPort.AuthResult result = loginUseCase.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        logoutUseCase.logout(jwt.getTokenValue());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-recovery-requests")
    public ResponseEntity<Void> requestRecovery(@org.springframework.validation.annotation.Validated @org.springframework.web.bind.annotation.RequestBody PasswordRecoveryRequest request) {
        passwordRecoveryUseCase.requestRecovery(request.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password-resets")
    public ResponseEntity<Void> resetPassword(@org.springframework.validation.annotation.Validated @org.springframework.web.bind.annotation.RequestBody PasswordResetRequest request) {
        passwordResetUseCase.reset(request.recoveryToken(), request.newPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(java.util.Map.of("userId", jwt.getSubject(), "email", jwt.getClaimAsString("email")));
    }
}
