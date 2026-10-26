package com.codefactory.bookingplatform.identity.application;

import com.codefactory.bookingplatform.auth.domain.port.UserProvisioning;
import com.codefactory.bookingplatform.identity.application.dto.RegisterClientCommand;
import com.codefactory.bookingplatform.identity.domain.model.Client;
import com.codefactory.bookingplatform.identity.domain.port.ClientRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/**
 * HU-001. identity solo conoce la fachada UserProvisioning de auth, nunca
 * su infraestructura ni su API (regla verificada en ArchitectureTest).
 */
@Service
public class RegisterClientUseCase {

    private final ClientRepository clientRepository;
    private final UserProvisioning userProvisioning;

    public RegisterClientUseCase(ClientRepository clientRepository, UserProvisioning userProvisioning) {
        this.clientRepository = clientRepository;
        this.userProvisioning = userProvisioning;
    }

    @Transactional
    public Client register(RegisterClientCommand command) {
        if (clientRepository.existsByDocument(command.document())) {
            throw BusinessException.duplicate("Ya existe una cuenta registrada con ese documento");
        }
        if (clientRepository.existsByEmail(command.email())) {
            throw BusinessException.duplicate("Ya existe una cuenta registrada con ese correo");
        }

        UUID userId = userProvisioning.provisionUser(command.email(), command.password());

        Client client = Client.register(userId, command.fullName(), command.document(), command.birthDate(),
                command.email(), command.phone(), command.city(), command.notificationChannel(), LocalDate.now());

        return clientRepository.save(client);
    }
}
