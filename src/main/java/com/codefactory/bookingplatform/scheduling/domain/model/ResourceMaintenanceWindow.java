package com.codefactory.bookingplatform.scheduling.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.time.Instant;
import java.util.UUID;

public record ResourceMaintenanceWindow(UUID id, UUID resourceId, Instant startsAt, Instant endsAt) {
    public ResourceMaintenanceWindow {
        if (startsAt == null || endsAt == null || !endsAt.isAfter(startsAt)) {
            throw BusinessException.rule("El rango de mantenimiento no es valido");
        }
    }

    public boolean overlaps(Instant rangeStart, Instant rangeEnd) {
        return startsAt.isBefore(rangeEnd) && endsAt.isAfter(rangeStart);
    }
}
