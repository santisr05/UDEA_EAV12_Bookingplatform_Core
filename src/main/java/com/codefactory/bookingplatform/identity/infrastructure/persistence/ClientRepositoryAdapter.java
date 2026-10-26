package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import com.codefactory.bookingplatform.identity.domain.model.Client;
import com.codefactory.bookingplatform.identity.domain.model.ClientStatus;
import com.codefactory.bookingplatform.identity.domain.port.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    public ClientRepositoryAdapter(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Client save(Client client) {
        ClientJpaEntity entity = new ClientJpaEntity(client.getId(), client.getFullName(), client.getDocument(),
                client.getBirthDate(), client.getEmail(), client.getPhone(), client.getCity(),
                client.getNotificationChannel(), client.isRemindersEnabled(), client.getStatus().name(),
                client.getCreatedAt());
        jpaRepository.save(entity);
        return client;
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> Client.rehydrate(e.getId(), e.getFullName(), e.getDocument(),
                e.getBirthDate(), e.getEmail(), e.getPhone(), e.getCity(), e.getNotificationChannel(),
                e.isRemindersEnabled(), ClientStatus.valueOf(e.getStatus()), e.getCreatedAt()));
    }

    @Override
    public boolean existsByDocument(String document) {
        return jpaRepository.existsByDocument(document);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
