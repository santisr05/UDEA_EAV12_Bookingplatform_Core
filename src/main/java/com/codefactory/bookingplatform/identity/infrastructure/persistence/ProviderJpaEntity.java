package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "providers")
public class ProviderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "legal_name", nullable = false, length = 200)
    private String legalName;

    @Column(nullable = false, unique = true, length = 30)
    private String nit;

    @Column(length = 60)
    private String category;

    @Column(name = "contact_email", nullable = false, length = 160)
    private String contactEmail;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(nullable = false, length = 60)
    private String timezone;

    @Column(nullable = false, length = 25)
    private String status;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "min_booking_notice_minutes", nullable = false)
    private int minBookingNoticeMinutes;

    @Column(name = "max_booking_notice_minutes", nullable = false)
    private int maxBookingNoticeMinutes;

    @Column(name = "free_cancellation_window_minutes", nullable = false)
    private int freeCancellationWindowMinutes;

    protected ProviderJpaEntity() {
    }

    public ProviderJpaEntity(UUID id, UUID userId, String legalName, String nit, String category,
                              String contactEmail, String contactPhone, String timezone, String status,
                              Instant approvedAt, int minNotice, int maxNotice, int freeCancellationWindow) {
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

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getLegalName() { return legalName; }
    public String getNit() { return nit; }
    public String getCategory() { return category; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public String getTimezone() { return timezone; }
    public String getStatus() { return status; }
    public Instant getApprovedAt() { return approvedAt; }
    public int getMinBookingNoticeMinutes() { return minBookingNoticeMinutes; }
    public int getMaxBookingNoticeMinutes() { return maxBookingNoticeMinutes; }
    public int getFreeCancellationWindowMinutes() { return freeCancellationWindowMinutes; }
}
