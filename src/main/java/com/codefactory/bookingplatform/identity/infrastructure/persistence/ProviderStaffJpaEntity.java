package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "provider_staff", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "provider_id"}))
public class ProviderStaffJpaEntity {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(nullable = false, length = 30)
    private String role;

    protected ProviderStaffJpaEntity() {
    }

    public ProviderStaffJpaEntity(UUID id, UUID userId, UUID providerId, String role) {
        this.id = id;
        this.userId = userId;
        this.providerId = providerId;
        this.role = role;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getProviderId() { return providerId; }
    public String getRole() { return role; }
}
