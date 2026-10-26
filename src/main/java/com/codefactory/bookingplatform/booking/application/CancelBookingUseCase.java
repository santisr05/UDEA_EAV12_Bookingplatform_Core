package com.codefactory.bookingplatform.booking.application;

import com.codefactory.bookingplatform.booking.domain.model.Booking;
import com.codefactory.bookingplatform.booking.domain.model.BookingConditionSnapshot;
import com.codefactory.bookingplatform.booking.domain.model.BookingStatusChange;
import com.codefactory.bookingplatform.booking.domain.port.BookingConditionSnapshotRepository;
import com.codefactory.bookingplatform.booking.domain.port.BookingRepository;
import com.codefactory.bookingplatform.booking.domain.port.BookingStatusChangeRepository;
import com.codefactory.bookingplatform.scheduling.application.ReserveResourcesUseCase;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/** HU-013. */
@Service
public class CancelBookingUseCase {

    private final BookingRepository bookingRepository;
    private final BookingConditionSnapshotRepository snapshotRepository;
    private final BookingStatusChangeRepository statusChangeRepository;
    private final ReserveResourcesUseCase reserveResourcesUseCase;

    public CancelBookingUseCase(BookingRepository bookingRepository,
                                 BookingConditionSnapshotRepository snapshotRepository,
                                 BookingStatusChangeRepository statusChangeRepository,
                                 ReserveResourcesUseCase reserveResourcesUseCase) {
        this.bookingRepository = bookingRepository;
        this.snapshotRepository = snapshotRepository;
        this.statusChangeRepository = statusChangeRepository;
        this.reserveResourcesUseCase = reserveResourcesUseCase;
    }

    public record Result(boolean late) {}

    @Transactional
    public Result cancelByClient(UUID bookingId, UUID clientId) {
        Booking booking = loadOwnedByClient(bookingId, clientId);
        BookingConditionSnapshot snapshot = snapshotFor(bookingId);

        var fromStatus = booking.getStatus();
        boolean late = booking.cancelByClient(snapshot, Instant.now());
        persist(booking, fromStatus, clientId, late ? "Cancelacion tardia por el cliente" : "Cancelacion por el cliente");
        return new Result(late);
    }

    @Transactional
    public void cancelByProvider(UUID bookingId, UUID providerUserId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.notFound("Reserva no encontrada"));
        var fromStatus = booking.getStatus();
        booking.cancelByProvider(reason);
        persist(booking, fromStatus, providerUserId, reason);
    }

    private Booking loadOwnedByClient(UUID bookingId, UUID clientId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.notFound("Reserva no encontrada"));
        if (!booking.getClientId().equals(clientId)) {
            throw BusinessException.forbidden("No puede gestionar una reserva de otro cliente");
        }
        return booking;
    }

    private BookingConditionSnapshot snapshotFor(UUID bookingId) {
        return snapshotRepository.findByBookingId(bookingId)
                .orElseThrow(() -> BusinessException.notFound("No se encontraron las condiciones de la reserva"));
    }

    private void persist(Booking booking, com.codefactory.bookingplatform.booking.domain.model.BookingStatus from,
                          UUID changedBy, String reason) {
        bookingRepository.save(booking);
        reserveResourcesUseCase.release(booking.getId());
        statusChangeRepository.save(new BookingStatusChange(UUID.randomUUID(), booking.getId(), from,
                booking.getStatus(), reason, changedBy, Instant.now()));
    }
}
