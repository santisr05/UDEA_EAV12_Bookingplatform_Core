package com.codefactory.bookingplatform.auth.application;

import com.codefactory.bookingplatform.auth.domain.model.LoginAttempt;
import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import com.codefactory.bookingplatform.auth.domain.port.LoginAttemptRepository;
import com.codefactory.bookingplatform.auth.domain.service.LoginLockPolicy;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import com.codefactory.bookingplatform.shared.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LoginUseCase {

    private final IdentityProviderPort identityProviderPort;
    private final LoginAttemptRepository loginAttemptRepository;

    public LoginUseCase(IdentityProviderPort identityProviderPort, LoginAttemptRepository loginAttemptRepository) {
        this.identityProviderPort = identityProviderPort;
        this.loginAttemptRepository = loginAttemptRepository;
    }

    public IdentityProviderPort.AuthResult login(String email, String password) {
        var recent = loginAttemptRepository.findRecentByEmail(email, LoginLockPolicy.MAX_ATTEMPTS + 5);
        if (LoginLockPolicy.isLocked(recent, Instant.now())) {
            throw new BusinessException(ErrorCode.AUTH_ACCOUNT_LOCKED, HttpStatus.TOO_MANY_REQUESTS,
                    "La cuenta esta bloqueada temporalmente por varios intentos fallidos");
        }

        try {
            IdentityProviderPort.AuthResult result = identityProviderPort.login(email, password);
            loginAttemptRepository.record(new LoginAttempt(email, true, Instant.now()));
            return result;
        } catch (RuntimeException ex) {
            loginAttemptRepository.record(new LoginAttempt(email, false, Instant.now()));
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED,
                    "Correo o contrasena incorrectos");
        }
    }
}
