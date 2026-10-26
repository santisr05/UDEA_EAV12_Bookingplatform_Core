package com.codefactory.bookingplatform.booking.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Agregado de reserva (HU-012, HU-013, HU-014). El codigo se genera en
 * memoria con un prefijo legible + sufijo aleatorio; la unicidad real la
 * garantiza la restriccion UNIQUE de la columna en el modelo fisico.
 */
public class Booking {

    private final UUID id;
    private final UUID clientId;
    private final UUID serviceId;
    private final UUID locationId;
    private final String code;
    private Instant startsAt;
    private Instant endsAt;
    private final int attendees;
    private final BigDecimal totalPrice;
    private BookingStatus status;
    private int rescheduleCount;
    private final boolean manualConfirmationRequired;
    private final Instant createdAt;

    private Booking(UUID id, UUID clientId, UUID serviceId, UUID locationId, String code, Instant startsAt,
                     Instant endsAt, int attendees, BigDecimal totalPrice, BookingStatus status, int rescheduleCount,
                     boolean manualConfirmationRequired, Instant createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.serviceId = serviceId;
        this.locationId = locationId;
        this.code = code;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.attendees = attendees;
        this.totalPrice = totalPrice;
        this.status = status;
        this.rescheduleCount = rescheduleCount;
        this.manualConfirmationRequired = manualConfirmationRequired;
        this.createdAt = createdAt;
    }

    public static Booking create(UUID id, UUID clientId, UUID serviceId, UUID locationId, Instant startsAt,
                                  Instant endsAt, int attendees, BigDecimal totalPrice,
                                  boolean manualConfirmationRequired) {
        if (!endsAt.isAfter(startsAt)) {
            throw BusinessException.rule("La hora de fin debe ser posterior a la de inicio");
        }
        String code = generateCode();
        BookingStatus initialStatus = manualConfirmationRequired ? BookingStatus.PENDING : BookingStatus.CONFIRMED;
        return new Booking(id, clientId, serviceId, locationId, code, startsAt, endsAt, attendees, totalPrice,
                initialStatus, 0, manualConfirmationRequired, Instant.now());
    }

    public static Booking rehydrate(UUID id, UUID clientId, UUID serviceId, UUID locationId, String code,
                                     Instant startsAt, Instant endsAt, int attendees, BigDecimal totalPrice,
                                     BookingStatus status, int rescheduleCount, boolean manualConfirmationRequired,
                                     Instant createdAt) {
        return new Booking(id, clientId, serviceId, locationId, code, startsAt, endsAt, attendees, totalPrice,
                status, rescheduleCount, manualConfirmationRequired, createdAt);
    }

    private static String generateCode() {
        return "RSV-" + Long.toString(Math.abs(UUID.randomUUID().getMostSignificantBits()), 36).toUpperCase();
    }

    /** HU-013. Devuelve si la cancelacion queda marcada como tardia (aplica penalidad). */
    public boolean cancelByClient(BookingConditionSnapshot snapshot, Instant now) {
        ensureCancellable();
        boolean late = Duration.between(now, startsAt).toMinutes() < snapshot.freeCancellationWindowMinutes();
        this.status = late ? BookingStatus.CANCELLED_LATE : BookingStatus.CANCELLED;
        return late;
    }

    /** HU-013, "Cancelacion por el proveedor exige motivo": siempre sin penalidad para el cliente. */
    public void cancelByProvider(String reason) {
        if (reason == null || reason.isBlank()) {
            throw BusinessException.rule("El motivo de cancelacion es obligatorio");
        }
        ensureCancellable();
        this.status = BookingStatus.CANCELLED;
    }

    private void ensureCancellable() {
        if (status != BookingStatus.PENDING && status != BookingStatus.CONFIRMED) {
            throw BusinessException.invalidState(
                    "Solo una reserva pendiente o confirmada puede cancelarse (estado actual: " + status + ")");
        }
    }

    /** HU-014. true si queda marcada como reprogramacion tardia. */
    public boolean reschedule(Instant newStartsAt, Instant newEndsAt, BookingConditionSnapshot snapshot, Instant now) {
        if (status != BookingStatus.CONFIRMED && status != BookingStatus.PENDING) {
            throw BusinessException.invalidState("Solo una reserva pendiente o confirmada puede reprogramarse");
        }
        if (rescheduleCount >= snapshot.maxReschedules()) {
            throw BusinessException.rule(
                    "Se alcanzo el numero maximo de reprogramaciones permitido; cancele y cree una nueva reserva");
        }
        if (!newEndsAt.isAfter(newStartsAt)) {
            throw BusinessException.rule("La hora de fin debe ser posterior a la de inicio");
        }
        boolean late = Duration.between(now, startsAt).toMinutes() < snapshot.freeCancellationWindowMinutes();
        this.startsAt = newStartsAt;
        this.endsAt = newEndsAt;
        this.rescheduleCount++;
        return late;
    }

    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public UUID getServiceId() { return serviceId; }
    public UUID getLocationId() { return locationId; }
    public String getCode() { return code; }
    public Instant getStartsAt() { return startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public int getAttendees() { return attendees; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public BookingStatus getStatus() { return status; }
    public int getRescheduleCount() { return rescheduleCount; }
    public boolean isManualConfirmationRequired() { return manualConfirmationRequired; }
    public Instant getCreatedAt() { return createdAt; }
}
