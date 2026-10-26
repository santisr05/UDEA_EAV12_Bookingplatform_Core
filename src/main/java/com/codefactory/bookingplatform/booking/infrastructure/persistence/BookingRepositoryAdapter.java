package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import com.codefactory.bookingplatform.booking.domain.model.Booking;
import com.codefactory.bookingplatform.booking.domain.model.BookingStatus;
import com.codefactory.bookingplatform.booking.domain.port.BookingRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class BookingRepositoryAdapter implements BookingRepository {

    private final BookingJpaRepository jpaRepository;

    public BookingRepositoryAdapter(BookingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Booking save(Booking booking) {
        jpaRepository.save(new BookingJpaEntity(booking.getId(), booking.getClientId(), booking.getServiceId(),
                booking.getLocationId(), booking.getCode(), booking.getStartsAt(), booking.getEndsAt(),
                booking.getAttendees(), booking.getTotalPrice(), booking.getStatus().name(),
                booking.getRescheduleCount(), booking.isManualConfirmationRequired(), booking.getCreatedAt()));
        return booking;
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> Booking.rehydrate(e.getId(), e.getClientId(), e.getServiceId(),
                e.getLocationId(), e.getCode(), e.getStartsAt(), e.getEndsAt(), e.getAttendees(), e.getTotalPrice(),
                BookingStatus.valueOf(e.getStatus()), e.getRescheduleCount(), e.isManualConfirmationRequired(),
                e.getCreatedAt()));
    }

    @Override
    public boolean existsOverlapForClient(UUID clientId, Instant startsAt, Instant endsAt) {
        return jpaRepository.existsOverlapForClient(clientId, startsAt, endsAt);
    }

    @Override
    public long countNoShowsSince(UUID clientId, Instant since) {
        return jpaRepository.countNoShowsSince(clientId, since);
    }
}
