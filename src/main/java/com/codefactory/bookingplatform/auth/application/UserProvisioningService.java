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
        // TODO: identityProviderPort.triggerEmailVerification(email) deshabilitado
        // temporalmente - el endpoint /resend de Supabase rechaza con 400 cuando
        // el usuario se creo via /admin/users (en vez del flujo publico de signup).
        // Pendiente investigar el contrato exacto despues del video; mientras
        // tanto el registro no queda bloqueado por este paso.
        return identityProviderPort.createUser(email, password);
    }
}
