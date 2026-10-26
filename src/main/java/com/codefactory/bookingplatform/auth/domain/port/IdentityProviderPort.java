package com.codefactory.bookingplatform.auth.domain.port;

import java.util.UUID;

/**
 * Puerto hacia el proveedor externo de autenticacion (Supabase Auth / GoTrue).
 * El resto del sistema nunca llama al SDK del proveedor directamente
 * (ADR-03): solo conoce esta interfaz.
 */
public interface IdentityProviderPort {

    UUID createUser(String email, String password);

    void triggerEmailVerification(String email);

    void verifyEmailOtp(String email, String token);

    AuthResult login(String email, String password);

    void logout(String accessToken);

    void requestPasswordRecovery(String email);

    void resetPassword(String recoveryToken, String newPassword);

    record AuthResult(String accessToken, String refreshToken, UUID userId) {
    }
}
