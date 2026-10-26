package com.codefactory.bookingplatform.scheduling.domain.port;

import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ScheduleExceptionRepository {
    ScheduleException save(ScheduleException exception);
    List<ScheduleException> findByScheduleIdAndRange(UUID scheduleId, Instant from, Instant to);
}
