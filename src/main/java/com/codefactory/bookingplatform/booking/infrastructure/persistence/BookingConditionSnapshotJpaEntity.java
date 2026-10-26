package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "booking_condition_snapshot")
public class BookingConditionSnapshotJpaEntity {
    @Id
    @Column(name = "booking_id")
    private UUID bookingId;

    @Column(name = "min_lead_time_minutes", nullable = false)
    private int minLeadTimeMinutes;

    @Column(name = "max_lead_time_minutes", nullable = false)
    private int maxLeadTimeMinutes;

    @Column(name = "free_cancellation_window_minutes", nullable = false)
    private int freeCancellationWindowMinutes;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "max_reschedules", nullable = false)
    private int maxReschedules;

    protected BookingConditionSnapshotJpaEntity() {
    }

    public BookingConditionSnapshotJpaEntity(UUID bookingId, int minLeadTimeMinutes, int maxLeadTimeMinutes,
                                              int freeCancellationWindowMinutes, int durationMinutes, int maxReschedules) {
        this.bookingId = bookingId;
        this.minLeadTimeMinutes = minLeadTimeMinutes;
        this.maxLeadTimeMinutes = maxLeadTimeMinutes;
        this.freeCancellationWindowMinutes = freeCancellationWindowMinutes;
        this.durationMinutes = durationMinutes;
        this.maxReschedules = maxReschedules;
    }

    public UUID getBookingId() { return bookingId; }
    public int getMinLeadTimeMinutes() { return minLeadTimeMinutes; }
    public int getMaxLeadTimeMinutes() { return maxLeadTimeMinutes; }
    public int getFreeCancellationWindowMinutes() { return freeCancellationWindowMinutes; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getMaxReschedules() { return maxReschedules; }
}
