package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import com.codefactory.bookingplatform.booking.domain.model.BookingConditionSnapshot;
import com.codefactory.bookingplatform.booking.domain.port.BookingConditionSnapshotRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class BookingConditionSnapshotRepositoryAdapter implements BookingConditionSnapshotRepository {

    private final BookingConditionSnapshotJpaRepository jpaRepository;

    public BookingConditionSnapshotRepositoryAdapter(BookingConditionSnapshotJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BookingConditionSnapshot save(BookingConditionSnapshot snapshot) {
        jpaRepository.save(new BookingConditionSnapshotJpaEntity(snapshot.bookingId(), snapshot.minLeadTimeMinutes(),
                snapshot.maxLeadTimeMinutes(), snapshot.freeCancellationWindowMinutes(), snapshot.durationMinutes(),
                snapshot.maxReschedules()));
        return snapshot;
    }

    @Override
    public Optional<BookingConditionSnapshot> findByBookingId(UUID bookingId) {
        return jpaRepository.findById(bookingId).map(e -> new BookingConditionSnapshot(e.getBookingId(),
                e.getMinLeadTimeMinutes(), e.getMaxLeadTimeMinutes(), e.getFreeCancellationWindowMinutes(),
                e.getDurationMinutes(), e.getMaxReschedules()));
    }
}
