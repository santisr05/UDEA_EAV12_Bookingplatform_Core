package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "provider_owners")
public class ProviderOwnerJpaEntity {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "provider_id", nullable = false, unique = true)
    private UUID providerId;

    protected ProviderOwnerJpaEntity() {
    }

    public ProviderOwnerJpaEntity(UUID id, UUID userId, UUID providerId) {
        this.id = id;
        this.userId = userId;
        this.providerId = providerId;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getProviderId() { return providerId; }
}
