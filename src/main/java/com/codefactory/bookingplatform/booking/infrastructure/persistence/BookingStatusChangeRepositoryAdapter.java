package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import com.codefactory.bookingplatform.booking.domain.model.BookingStatusChange;
import com.codefactory.bookingplatform.booking.domain.port.BookingStatusChangeRepository;
import org.springframework.stereotype.Component;

@Component
public class BookingStatusChangeRepositoryAdapter implements BookingStatusChangeRepository {

    private final BookingStatusChangeJpaRepository jpaRepository;

    public BookingStatusChangeRepositoryAdapter(BookingStatusChangeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BookingStatusChange save(BookingStatusChange change) {
        jpaRepository.save(new BookingStatusChangeJpaEntity(change.id(), change.bookingId(), change.fromStatus().name(),
                change.toStatus().name(), change.reason(), change.changedBy(), change.changedAt()));
        return change;
    }
}
