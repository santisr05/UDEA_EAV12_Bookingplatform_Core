package com.codefactory.bookingplatform.identity.application;

import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import org.springframework.stereotype.Service;

@Service
public class ResendVerificationUseCase {

    private final IdentityProviderPort identityProviderPort;

    public ResendVerificationUseCase(IdentityProviderPort identityProviderPort) {
        this.identityProviderPort = identityProviderPort;
    }

    public void resend(String email) {
        identityProviderPort.triggerEmailVerification(email);
    }
}
