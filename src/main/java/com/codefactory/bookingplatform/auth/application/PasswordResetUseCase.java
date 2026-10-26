package com.codefactory.bookingplatform.auth.application;

import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import com.codefactory.bookingplatform.auth.domain.service.PasswordPolicy;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetUseCase {

    private final IdentityProviderPort identityProviderPort;

    public PasswordResetUseCase(IdentityProviderPort identityProviderPort) {
        this.identityProviderPort = identityProviderPort;
    }

    public void reset(String recoveryToken, String newPassword) {
        if (!PasswordPolicy.isValid(newPassword)) {
            throw BusinessException.rule(
                    "La contrasena debe tener minimo 8 caracteres, con mayuscula, minuscula y digito");
        }
        identityProviderPort.resetPassword(recoveryToken, newPassword);
    }
}
