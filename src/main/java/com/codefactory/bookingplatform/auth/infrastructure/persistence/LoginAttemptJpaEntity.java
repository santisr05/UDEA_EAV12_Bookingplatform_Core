package com.codefactory.bookingplatform.auth.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "login_attempts")
public class LoginAttemptJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false)
    private boolean successful;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected LoginAttemptJpaEntity() {
    }

    public LoginAttemptJpaEntity(String email, boolean successful, Instant occurredAt) {
        this.email = email;
        this.successful = successful;
        this.occurredAt = occurredAt;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public boolean isSuccessful() { return successful; }
    public Instant getOccurredAt() { return occurredAt; }
}
