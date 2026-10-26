package com.codefactory.bookingplatform.scheduling.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.time.LocalDate;
import java.util.UUID;

public class Schedule {

    public enum Status { ACTIVE, INACTIVE }

    private final UUID id;
    private final UUID locationId;
    private final UUID resourceId;
    private int granularityMinutes;
    private final LocalDate validFrom;
    private LocalDate validTo;
    private Status status;

    private Schedule(UUID id, UUID locationId, UUID resourceId, int granularityMinutes, LocalDate validFrom,
                      LocalDate validTo, Status status) {
        this.id = id;
        this.locationId = locationId;
        this.resourceId = resourceId;
        this.granularityMinutes = granularityMinutes;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
    }

    private static final int[] VALID_GRANULARITIES = {15, 30, 60};

    public static Schedule create(UUID id, UUID locationId, UUID resourceId, int granularityMinutes,
                                   LocalDate validFrom, LocalDate validTo) {
        validateGranularity(granularityMinutes);
        if (validTo != null && !validTo.isAfter(validFrom)) {
            throw BusinessException.rule("La fecha de vigencia final debe ser posterior a la inicial");
        }
        return new Schedule(id, locationId, resourceId, granularityMinutes, validFrom, validTo, Status.ACTIVE);
    }

    public static Schedule rehydrate(UUID id, UUID locationId, UUID resourceId, int granularityMinutes,
                                      LocalDate validFrom, LocalDate validTo, Status status) {
        return new Schedule(id, locationId, resourceId, granularityMinutes, validFrom, validTo, status);
    }

    private static void validateGranularity(int granularityMinutes) {
        for (int valid : VALID_GRANULARITIES) {
            if (valid == granularityMinutes) {
                return;
            }
        }
        throw BusinessException.rule("La granularidad debe ser 15, 30 o 60 minutos");
    }

    /**
     * HU-006, escenario "Cambio de granularidad con reservas futuras": el
     * llamador (el caso de uso) debe verificar con booking si existen
     * reservas futuras antes de invocar este metodo; aqui solo se valida el
     * nuevo valor en si.
     */
    public void changeGranularity(int newGranularityMinutes) {
        validateGranularity(newGranularityMinutes);
        this.granularityMinutes = newGranularityMinutes;
    }

    public UUID getId() { return id; }
    public UUID getLocationId() { return locationId; }
    public UUID getResourceId() { return resourceId; }
    public int getGranularityMinutes() { return granularityMinutes; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public Status getStatus() { return status; }
}
