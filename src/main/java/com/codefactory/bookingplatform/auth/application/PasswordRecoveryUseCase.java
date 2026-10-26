package com.codefactory.bookingplatform.auth.application;

import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import org.springframework.stereotype.Service;

/**
 * Siempre responde de la misma forma exista o no el correo (anti-enumeracion),
 * asi que esta clase no lanza error si el correo no existe: la decision de
 * no revelarlo vive en el controller, que responde 202 sin importar el resultado.
 */
@Service
public class PasswordRecoveryUseCase {

    private final IdentityProviderPort identityProviderPort;

    public PasswordRecoveryUseCase(IdentityProviderPort identityProviderPort) {
        this.identityProviderPort = identityProviderPort;
    }

    public void requestRecovery(String email) {
        try {
            identityProviderPort.requestPasswordRecovery(email);
        } catch (RuntimeException ignored) {
            // No se revela si el correo existe o no.
        }
    }
}
