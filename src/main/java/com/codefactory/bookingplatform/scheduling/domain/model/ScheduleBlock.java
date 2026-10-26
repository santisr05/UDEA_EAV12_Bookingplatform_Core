package com.codefactory.bookingplatform.scheduling.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.time.LocalTime;
import java.util.UUID;

public record ScheduleBlock(UUID id, UUID scheduleId, int dayOfWeek, LocalTime startTime, LocalTime endTime) {

    public ScheduleBlock {
        if (dayOfWeek < 0 || dayOfWeek > 6) {
            throw BusinessException.rule("El dia de la semana debe estar entre 0 y 6");
        }
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw BusinessException.rule("La hora de fin debe ser posterior a la hora de inicio");
        }
    }

    public boolean overlaps(ScheduleBlock other) {
        if (this.dayOfWeek != other.dayOfWeek) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }
}
