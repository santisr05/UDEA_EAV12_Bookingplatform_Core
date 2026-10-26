package com.codefactory.bookingplatform.identity.application;

import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import com.codefactory.bookingplatform.identity.domain.model.Client;
import com.codefactory.bookingplatform.identity.domain.port.ClientRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConfirmClientEmailUseCase {

    private final IdentityProviderPort identityProviderPort;
    private final ClientRepository clientRepository;

    public ConfirmClientEmailUseCase(IdentityProviderPort identityProviderPort, ClientRepository clientRepository) {
        this.identityProviderPort = identityProviderPort;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public void confirm(UUID clientId, String email, String otpToken) {
        identityProviderPort.verifyEmailOtp(email, otpToken);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> BusinessException.notFound("Cliente no encontrado"));
        client.markVerified();
        clientRepository.save(client);
    }
}
