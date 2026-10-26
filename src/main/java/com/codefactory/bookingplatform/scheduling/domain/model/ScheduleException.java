package com.codefactory.bookingplatform.scheduling.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.time.Instant;
import java.util.UUID;

public record ScheduleException(UUID id, UUID scheduleId, ExceptionType type, Instant startsAt, Instant endsAt,
                                  String reason, boolean recurring) {

    public ScheduleException {
        if (startsAt == null || endsAt == null || !endsAt.isAfter(startsAt)) {
            throw BusinessException.rule("El rango de la excepcion no es valido");
        }
    }

    public static ScheduleException createBlock(UUID id, UUID scheduleId, Instant startsAt, Instant endsAt,
                                                  String reason, boolean recurring, Instant now) {
        if (startsAt.isBefore(now)) {
            throw BusinessException.rule("No se permite crear un bloqueo con fecha de inicio en el pasado");
        }
        return new ScheduleException(id, scheduleId, ExceptionType.BLOCK, startsAt, endsAt, reason, recurring);
    }

    public static ScheduleException createSpecialHours(UUID id, UUID scheduleId, Instant startsAt, Instant endsAt,
                                                         String reason) {
        return new ScheduleException(id, scheduleId, ExceptionType.SPECIAL_HOURS, startsAt, endsAt, reason, false);
    }

    public boolean overlaps(Instant rangeStart, Instant rangeEnd) {
        return startsAt.isBefore(rangeEnd) && endsAt.isAfter(rangeStart);
    }
}
