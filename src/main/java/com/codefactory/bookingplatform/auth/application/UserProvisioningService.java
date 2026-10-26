package com.codefactory.bookingplatform.auth.application;

import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import com.codefactory.bookingplatform.auth.domain.port.UserProvisioning;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Unica implementacion visible de UserProvisioning: es la puerta por la que
 * identity crea usuarios en Supabase Auth sin conocer el resto de auth.
 */
@Service
public class UserProvisioningService implements UserProvisioning {

    private final IdentityProviderPort identityProviderPort;

    public UserProvisioningService(IdentityProviderPort identityProviderPort) {
        this.identityProviderPort = identityProviderPort;
    }

    @Override
    public UUID provisionUser(String email, String password) {
        UUID userId = identityProviderPort.createUser(email, password);
        identityProviderPort.triggerEmailVerification(email);
        return userId;
    }
}
