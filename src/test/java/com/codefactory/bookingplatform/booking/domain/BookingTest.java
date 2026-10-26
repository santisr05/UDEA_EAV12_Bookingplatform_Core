package com.codefactory.bookingplatform.booking.domain;

import com.codefactory.bookingplatform.booking.domain.model.Booking;
import com.codefactory.bookingplatform.booking.domain.model.BookingConditionSnapshot;
import com.codefactory.bookingplatform.booking.domain.model.BookingStatus;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingTest {

    private Booking newConfirmedBooking(Instant startsAt) {
        return Booking.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                startsAt, startsAt.plus(1, ChronoUnit.HOURS), 1, BigDecimal.TEN, false);
    }

    private BookingConditionSnapshot snapshot(UUID bookingId, int freeCancellationWindowMinutes) {
        return new BookingConditionSnapshot(bookingId, 0, 43200, freeCancellationWindowMinutes, 60, 3);
    }

    @Test
    void create_without_manual_confirmation_starts_confirmed() {
        Booking booking = newConfirmedBooking(Instant.now().plusSeconds(3600));
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void create_with_manual_confirmation_required_starts_pending() {
        Booking booking = Booking.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), 1, BigDecimal.TEN, true);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void cancel_within_free_window_has_no_penalty() {
        Instant startsAt = Instant.now().plus(2, ChronoUnit.DAYS);
        Booking booking = newConfirmedBooking(startsAt);
        boolean late = booking.cancelByClient(snapshot(booking.getId(), 60), Instant.now());

        assertThat(late).isFalse();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancel_outside_free_window_is_marked_late() {
        Instant startsAt = Instant.now().plus(30, ChronoUnit.MINUTES);
        Booking booking = newConfirmedBooking(startsAt);
        boolean late = booking.cancelByClient(snapshot(booking.getId(), 1440), Instant.now()); // ventana de 24h

        assertThat(late).isTrue();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED_LATE);
    }

    @Test
    void cannot_cancel_a_completed_booking() {
        Booking booking = newConfirmedBooking(Instant.now().plusSeconds(3600));
        booking.cancelByClient(snapshot(booking.getId(), 60), Instant.now());

        assertThatThrownBy(() -> booking.cancelByClient(snapshot(booking.getId(), 60), Instant.now()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void cancel_by_provider_requires_reason() {
        Booking booking = newConfirmedBooking(Instant.now().plusSeconds(3600));
        assertThatThrownBy(() -> booking.cancelByProvider(" "))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void reschedule_increments_count_and_moves_dates() {
        Instant startsAt = Instant.now().plus(2, ChronoUnit.DAYS);
        Booking booking = newConfirmedBooking(startsAt);
        Instant newStart = startsAt.plus(1, ChronoUnit.DAYS);

        boolean late = booking.reschedule(newStart, newStart.plus(1, ChronoUnit.HOURS), snapshot(booking.getId(), 60),
                Instant.now());

        assertThat(late).isFalse();
        assertThat(booking.getStartsAt()).isEqualTo(newStart);
        assertThat(booking.getRescheduleCount()).isEqualTo(1);
    }

    @Test
    void reschedule_beyond_limit_throws() {
        Instant startsAt = Instant.now().plus(10, ChronoUnit.DAYS);
        Booking booking = newConfirmedBooking(startsAt);
        BookingConditionSnapshot snap = snapshot(booking.getId(), 60);

        for (int i = 0; i < 3; i++) {
            Instant next = booking.getStartsAt().plus(1, ChronoUnit.DAYS);
            booking.reschedule(next, next.plus(1, ChronoUnit.HOURS), snap, Instant.now());
        }

        Instant oneMore = booking.getStartsAt().plus(1, ChronoUnit.DAYS);
        assertThatThrownBy(() -> booking.reschedule(oneMore, oneMore.plus(1, ChronoUnit.HOURS), snap, Instant.now()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("maximo de reprogramaciones");
    }
}
