package com.codefactory.bookingplatform.auth.application;

import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCase {

    private final IdentityProviderPort identityProviderPort;

    public LogoutUseCase(IdentityProviderPort identityProviderPort) {
        this.identityProviderPort = identityProviderPort;
    }

    public void logout(String accessToken) {
        identityProviderPort.logout(accessToken);
    }
}
