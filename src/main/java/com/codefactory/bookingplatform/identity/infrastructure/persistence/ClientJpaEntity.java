package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class ClientJpaEntity {

    @Id
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false, unique = true, length = 30)
    private String document;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 80)
    private String city;

    @Column(name = "notification_channel", nullable = false, length = 20)
    private String notificationChannel;

    @Column(name = "reminders_enabled", nullable = false)
    private boolean remindersEnabled;

    @Column(nullable = false, length = 25)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ClientJpaEntity() {
    }

    public ClientJpaEntity(UUID id, String fullName, String document, LocalDate birthDate, String email,
                            String phone, String city, String notificationChannel, boolean remindersEnabled,
                            String status, Instant createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.document = document;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.notificationChannel = notificationChannel;
        this.remindersEnabled = remindersEnabled;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getDocument() { return document; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getNotificationChannel() { return notificationChannel; }
    public boolean isRemindersEnabled() { return remindersEnabled; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
