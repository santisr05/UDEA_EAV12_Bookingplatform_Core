package com.codefactory.bookingplatform.scheduling.application;

import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleException;
import com.codefactory.bookingplatform.scheduling.domain.port.FutureBookingsQuery;
import com.codefactory.bookingplatform.scheduling.domain.port.ScheduleExceptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/** HU-007. */
@Service
public class CreateScheduleExceptionUseCase {

    private final ScheduleExceptionRepository scheduleExceptionRepository;
    private final FutureBookingsQuery futureBookingsQuery;

    public CreateScheduleExceptionUseCase(ScheduleExceptionRepository scheduleExceptionRepository,
                                           FutureBookingsQuery futureBookingsQuery) {
        this.scheduleExceptionRepository = scheduleExceptionRepository;
        this.futureBookingsQuery = futureBookingsQuery;
    }

    public record BlockResult(long affectedBookings, ScheduleException created) {}

    @Transactional
    public BlockResult createBlock(UUID scheduleId, Instant startsAt, Instant endsAt, String reason,
                                    boolean recurring, boolean force) {
        long affected = futureBookingsQuery.countFutureConfirmedBookingsForScheduleInRange(scheduleId, startsAt, endsAt);
        if (affected > 0 && !force) {
            return new BlockResult(affected, null);
        }
        ScheduleException exception = ScheduleException.createBlock(UUID.randomUUID(), scheduleId, startsAt, endsAt,
                reason, recurring, Instant.now());
        return new BlockResult(affected, scheduleExceptionRepository.save(exception));
    }

    @Transactional
    public ScheduleException createSpecialHours(UUID scheduleId, Instant startsAt, Instant endsAt, String reason) {
        ScheduleException exception = ScheduleException.createSpecialHours(UUID.randomUUID(), scheduleId, startsAt,
                endsAt, reason);
        return scheduleExceptionRepository.save(exception);
    }

    /** HU-007, "Carga masiva de festivos": crea un bloqueo recurrente de dia completo por cada fecha. */
    @Transactional
    public void loadHolidays(UUID scheduleId, java.util.List<java.time.LocalDate> holidays, java.time.ZoneId zone) {
        for (java.time.LocalDate holiday : holidays) {
            Instant start = holiday.atStartOfDay(zone).toInstant();
            Instant end = holiday.plusDays(1).atStartOfDay(zone).toInstant();
            createBlock(scheduleId, start, end, "Festivo nacional", false, true);
        }
    }
}
