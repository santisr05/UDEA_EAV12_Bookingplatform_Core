package com.codefactory.bookingplatform.booking.application;

import com.codefactory.bookingplatform.booking.domain.model.Booking;
import com.codefactory.bookingplatform.booking.domain.model.BookingConditionSnapshot;
import com.codefactory.bookingplatform.booking.domain.model.BookingStatus;
import com.codefactory.bookingplatform.booking.domain.model.BookingStatusChange;
import com.codefactory.bookingplatform.booking.domain.port.BookingConditionSnapshotRepository;
import com.codefactory.bookingplatform.booking.domain.port.BookingRepository;
import com.codefactory.bookingplatform.booking.domain.port.BookingStatusChangeRepository;
import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceOfferingRepository;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceResourceRequirementRepository;
import com.codefactory.bookingplatform.scheduling.application.ReserveResourcesUseCase;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * HU-014. "Todo o nada": se libera el horario anterior y se intenta asignar
 * el nuevo dentro de la MISMA transaccion; si ReserveResourcesUseCase falla
 * porque el nuevo horario ya no esta disponible, la excepcion revierte toda
 * la transaccion, incluida la liberacion previa, dejando la reserva
 * exactamente como estaba (el escenario "la reserva conserva su horario
 * original" se cumple por rollback, no por codigo compensatorio manual).
 */
@Service
public class RescheduleBookingUseCase {

    private final BookingRepository bookingRepository;
    private final BookingConditionSnapshotRepository snapshotRepository;
    private final BookingStatusChangeRepository statusChangeRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceResourceRequirementRepository requirementRepository;
    private final ReserveResourcesUseCase reserveResourcesUseCase;

    public RescheduleBookingUseCase(BookingRepository bookingRepository,
                                     BookingConditionSnapshotRepository snapshotRepository,
                                     BookingStatusChangeRepository statusChangeRepository,
                                     ServiceOfferingRepository serviceOfferingRepository,
                                     ServiceResourceRequirementRepository requirementRepository,
                                     ReserveResourcesUseCase reserveResourcesUseCase) {
        this.bookingRepository = bookingRepository;
        this.snapshotRepository = snapshotRepository;
        this.statusChangeRepository = statusChangeRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.requirementRepository = requirementRepository;
        this.reserveResourcesUseCase = reserveResourcesUseCase;
    }

    public record Result(boolean late) {}

    @Transactional
    public Result reschedule(UUID bookingId, UUID clientId, Instant newStartsAt, int scheduleGranularityMinutes) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.notFound("Reserva no encontrada"));
        if (!booking.getClientId().equals(clientId)) {
            throw BusinessException.forbidden("No puede gestionar una reserva de otro cliente");
        }

        BookingConditionSnapshot snapshot = snapshotRepository.findByBookingId(bookingId)
                .orElseThrow(() -> BusinessException.notFound("No se encontraron las condiciones de la reserva"));

        ServiceOffering service = serviceOfferingRepository.findById(booking.getServiceId())
                .orElseThrow(() -> BusinessException.notFound("Servicio no encontrado"));

        BookingStatus fromStatus = booking.getStatus();
        Instant newEndsAt = newStartsAt.plusSeconds(snapshot.durationMinutes() * 60L);

        // Libera primero: si la asignacion del nuevo horario falla mas abajo,
        // toda la transaccion se revierte y el lock anterior "vuelve" solo.
        reserveResourcesUseCase.release(booking.getId());

        boolean late = booking.reschedule(newStartsAt, newEndsAt, snapshot, Instant.now());

        List<ReserveResourcesUseCase.ResourceRequirement> required = requirementRepository.findByServiceId(booking.getServiceId())
                .stream()
                .map(r -> new ReserveResourcesUseCase.ResourceRequirement(r.resourceTypeId(), r.quantityRequired()))
                .toList();
        reserveResourcesUseCase.reserve(booking.getId(), required, newStartsAt, newEndsAt, scheduleGranularityMinutes);

        bookingRepository.save(booking);
        statusChangeRepository.save(new BookingStatusChange(UUID.randomUUID(), booking.getId(), fromStatus,
                booking.getStatus(), late ? "Reprogramacion tardia" : "Reprogramacion", clientId, Instant.now()));

        return new Result(late);
    }
}
