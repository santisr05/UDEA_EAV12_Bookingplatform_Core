package com.codefactory.bookingplatform.identity.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.time.Instant;
import java.util.UUID;

/**
 * Agregado de proveedor de servicios (HU-002). Queda pendiente de aprobacion
 * hasta que un administrador lo valida; mientras tanto no es visible en el
 * catalogo ni puede recibir reservas (lo exige HU-005).
 */
public class Provider {

    private final UUID id;
    private final UUID userId;
    private String legalName;
    private String nit;
    private String category;
    private String contactEmail;
    private String contactPhone;
    private String timezone;
    private ProviderStatus status;
    private Instant approvedAt;

    private int minBookingNoticeMinutes;
    private int maxBookingNoticeMinutes;
    private int freeCancellationWindowMinutes;

    private Provider(UUID id, UUID userId, String legalName, String nit, String category, String contactEmail,
                      String contactPhone, String timezone, ProviderStatus status, Instant approvedAt,
                      int minNotice, int maxNotice, int freeCancellationWindow) {
        this.id = id;
        this.userId = userId;
        this.legalName = legalName;
        this.nit = nit;
        this.category = category;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.timezone = timezone;
        this.status = status;
        this.approvedAt = approvedAt;
        this.minBookingNoticeMinutes = minNotice;
        this.maxBookingNoticeMinutes = maxNotice;
        this.freeCancellationWindowMinutes = freeCancellationWindow;
    }

    public static Provider register(UUID id, UUID userId, String legalName, String nit, String category,
                                     String contactEmail, String contactPhone, String timezone,
                                     int minNoticeMinutes, int maxNoticeMinutes, int freeCancellationWindowMinutes) {
        requireText(legalName, "legal_name");
        requireText(nit, "nit");
        requireText(timezone, "timezone");
        validateOperationPolicy(minNoticeMinutes, maxNoticeMinutes);
        return new Provider(id, userId, legalName, nit, category, contactEmail, contactPhone, timezone,
                ProviderStatus.PENDING_APPROVAL, null, minNoticeMinutes, maxNoticeMinutes, freeCancellationWindowMinutes);
    }

    public static Provider rehydrate(UUID id, UUID userId, String legalName, String nit, String category,
                                      String contactEmail, String contactPhone, String timezone,
                                      ProviderStatus status, Instant approvedAt, int minNotice, int maxNotice,
                                      int freeCancellationWindow) {
        return new Provider(id, userId, legalName, nit, category, contactEmail, contactPhone, timezone, status,
                approvedAt, minNotice, maxNotice, freeCancellationWindow);
    }

    private static void validateOperationPolicy(int minNotice, int maxNotice) {
        if (minNotice < 0 || maxNotice < 0) {
            throw BusinessException.rule("Los valores de anticipacion no pueden ser negativos");
        }
        if (minNotice > maxNotice) {
            throw BusinessException.rule("La anticipacion minima no puede ser mayor que la maxima");
        }
    }

    public void updateOperationPolicy(int minNoticeMinutes, int maxNoticeMinutes, int freeCancellationWindowMinutes) {
        validateOperationPolicy(minNoticeMinutes, maxNoticeMinutes);
        this.minBookingNoticeMinutes = minNoticeMinutes;
        this.maxBookingNoticeMinutes = maxNoticeMinutes;
        this.freeCancellationWindowMinutes = freeCancellationWindowMinutes;
        // Nota: estos cambios no afectan reservas ya creadas; cada reserva congela
        // sus propias condiciones en booking_condition_snapshot (ver modulo booking).
    }

    public void approve() {
        if (status != ProviderStatus.PENDING_APPROVAL) {
            throw BusinessException.invalidState("Solo un proveedor pendiente de aprobacion puede aprobarse");
        }
        this.status = ProviderStatus.ACTIVE;
        this.approvedAt = Instant.now();
    }

    public void suspend() {
        if (status == ProviderStatus.SUSPENDED) {
            throw BusinessException.invalidState("El proveedor ya esta suspendido");
        }
        this.status = ProviderStatus.SUSPENDED;
    }

    public boolean isVisibleInCatalog() {
        return status == ProviderStatus.ACTIVE;
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw BusinessException.rule("El campo " + field + " es obligatorio");
        }
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getLegalName() { return legalName; }
    public String getNit() { return nit; }
    public String getCategory() { return category; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public String getTimezone() { return timezone; }
    public ProviderStatus getStatus() { return status; }
    public Instant getApprovedAt() { return approvedAt; }
    public int getMinBookingNoticeMinutes() { return minBookingNoticeMinutes; }
    public int getMaxBookingNoticeMinutes() { return maxBookingNoticeMinutes; }
    public int getFreeCancellationWindowMinutes() { return freeCancellationWindowMinutes; }
}
