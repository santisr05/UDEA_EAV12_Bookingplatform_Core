package com.codefactory.bookingplatform.booking.infrastructure.persistence;

import com.codefactory.bookingplatform.catalog.domain.port.BookingLookupForCatalog;
import com.codefactory.bookingplatform.identity.domain.port.FutureConfirmedBookingsQuery;
import com.codefactory.bookingplatform.scheduling.domain.port.FutureBookingsQuery;
import com.codefactory.bookingplatform.scheduling.domain.port.ScheduleRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Implementa, desde booking, los puertos que identity, scheduling y catalog
 * definen para consultar reservas sin depender de booking en tiempo de
 * compilacion. Es el unico lugar donde booking "mira hacia atras".
 */
@Component
public class CrossModuleBookingQueryAdapter implements FutureConfirmedBookingsQuery, FutureBookingsQuery,
        BookingLookupForCatalog {

    private final BookingJpaRepository bookingJpaRepository;
    private final ScheduleRepository scheduleRepository;

    public CrossModuleBookingQueryAdapter(BookingJpaRepository bookingJpaRepository, ScheduleRepository scheduleRepository) {
        this.bookingJpaRepository = bookingJpaRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public long countFutureConfirmedBookings(UUID providerId) {
        return bookingJpaRepository.countFutureConfirmedByProvider(providerId, Instant.now());
    }

    @Override
    public long countFutureConfirmedBookingsForResource(UUID resourceId) {
        return bookingJpaRepository.countFutureConfirmedByResource(resourceId, Instant.now());
    }

    @Override
    public long countFutureConfirmedBookingsForSchedule(UUID scheduleId) {
        UUID locationId = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> BusinessException.notFound("Agenda no encontrada"))
                .getLocationId();
        return bookingJpaRepository.countFutureConfirmedByLocation(locationId, Instant.now());
    }

    @Override
    public long countFutureConfirmedBookingsForScheduleInRange(UUID scheduleId, Instant from, Instant to) {
        UUID locationId = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> BusinessException.notFound("Agenda no encontrada"))
                .getLocationId();
        return bookingJpaRepository.countFutureConfirmedByLocationInRange(locationId, from, to);
    }

    @Override
    public boolean hasAnyBooking(UUID serviceId) {
        return bookingJpaRepository.existsByServiceId(serviceId);
    }
}
