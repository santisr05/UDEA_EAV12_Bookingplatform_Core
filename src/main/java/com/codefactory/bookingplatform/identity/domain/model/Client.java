package com.codefactory.bookingplatform.identity.domain.model;

import com.codefactory.bookingplatform.identity.domain.service.AgePolicy;
import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Agregado de cliente (HU-001). El identificador es el mismo UUID que
 * Supabase Auth asigna al usuario; aqui solo vive el perfil de negocio.
 */
public class Client {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UUID id;
    private String fullName;
    private String document;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String city;
    private String notificationChannel;
    private boolean remindersEnabled;
    private ClientStatus status;
    private final Instant createdAt;

    private Client(UUID id, String fullName, String document, LocalDate birthDate, String email,
                    String phone, String city, String notificationChannel, boolean remindersEnabled,
                    ClientStatus status, Instant createdAt) {
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

    public static Client register(UUID id, String fullName, String document, LocalDate birthDate,
                                   String email, String phone, String city, String notificationChannel,
                                   LocalDate referenceDate) {
        Objects.requireNonNull(id, "id es obligatorio");
        requireText(fullName, "full_name");
        requireText(document, "document");
        requireText(email, "email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw BusinessException.rule("El correo no tiene un formato valido");
        }
        if (!AgePolicy.isOldEnough(birthDate, referenceDate)) {
            throw BusinessException.rule("El autorregistro requiere ser mayor de edad; se necesita un acudiente");
        }
        return new Client(id, fullName, document, birthDate, email, phone, city,
                notificationChannel == null ? "EMAIL" : notificationChannel, true,
                ClientStatus.PENDING_VERIFICATION, Instant.now());
    }

    public static Client rehydrate(UUID id, String fullName, String document, LocalDate birthDate, String email,
                                    String phone, String city, String notificationChannel, boolean remindersEnabled,
                                    ClientStatus status, Instant createdAt) {
        return new Client(id, fullName, document, birthDate, email, phone, city, notificationChannel,
                remindersEnabled, status, createdAt);
    }

    public void markVerified() {
        if (status != ClientStatus.PENDING_VERIFICATION) {
            throw BusinessException.invalidState("Solo una cuenta pendiente de verificacion puede verificarse");
        }
        this.status = ClientStatus.ACTIVE;
    }

    public void ensureCanBook() {
        if (status != ClientStatus.ACTIVE) {
            throw BusinessException.forbidden("La cuenta no esta verificada; verifique su correo para poder reservar");
        }
    }

    public void suspend() {
        if (status == ClientStatus.SUSPENDED) {
            throw BusinessException.invalidState("El cliente ya esta suspendido");
        }
        this.status = ClientStatus.SUSPENDED;
    }

    public void reactivate() {
        if (status != ClientStatus.SUSPENDED) {
            throw BusinessException.invalidState("Solo un cliente suspendido puede reactivarse");
        }
        this.status = ClientStatus.ACTIVE;
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw BusinessException.rule("El campo " + field + " es obligatorio");
        }
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
    public ClientStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
