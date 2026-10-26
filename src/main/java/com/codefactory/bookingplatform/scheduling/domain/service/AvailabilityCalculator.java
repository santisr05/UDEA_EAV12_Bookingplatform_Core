package com.codefactory.bookingplatform.scheduling.domain.service;

import com.codefactory.bookingplatform.scheduling.domain.model.ExceptionType;
import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleBlock;
import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HU-008 (generacion de franjas) y HU-011 (consulta en tiempo real).
 * Es una funcion pura: no conoce JPA ni Spring. Parte del horario regular,
 * le resta bloqueos y aplica horarios especiales, genera candidatos segun la
 * granularidad, y descarta los que no tengan al menos un recurso libre o que
 * no respeten la anticipacion minima/maxima del proveedor.
 */
public final class AvailabilityCalculator {

    private AvailabilityCalculator() {
    }

    private record Interval(Instant start, Instant end) {
        boolean contains(Instant otherStart, Instant otherEnd) {
            return !otherStart.isBefore(start) && !otherEnd.isAfter(end);
        }
    }

    public static List<AvailableSlot> calculate(AvailabilityRequest request) {
        List<AvailableSlot> result = new ArrayList<>();
        Duration slotDuration = Duration.ofMinutes(request.slotDurationMinutes());
        Duration granularity = Duration.ofMinutes(request.granularityMinutes());
        Instant earliestAllowed = request.now().plus(Duration.ofMinutes(request.minNoticeMinutes()));
        Instant latestAllowed = request.now().plus(Duration.ofMinutes(request.maxNoticeMinutes()));

        for (LocalDate date = request.rangeStart(); !date.isAfter(request.rangeEnd()); date = date.plusDays(1)) {
            List<Interval> workingIntervals = effectiveWorkingIntervals(date, request);
            List<Interval> blockedIntervals = blockedIntervalsForDay(date, request);

            for (Interval working : workingIntervals) {
                List<Interval> freeIntervals = subtractAll(working, blockedIntervals);
                for (Interval free : freeIntervals) {
                    Instant candidate = free.start();
                    while (!candidate.plus(slotDuration).isAfter(free.end())) {
                        Instant candidateEnd = candidate.plus(slotDuration);
                        boolean withinNotice = !candidate.isBefore(earliestAllowed) && !candidate.isAfter(latestAllowed);
                        if (withinNotice && request.hasFreeResource().test(candidate, candidateEnd)) {
                            result.add(new AvailableSlot(candidate, candidateEnd));
                        }
                        candidate = candidate.plus(granularity);
                    }
                }
            }
        }
        return result;
    }

    private static List<Interval> effectiveWorkingIntervals(LocalDate date, AvailabilityRequest request) {
        List<Interval> specialHours = request.exceptions().stream()
                .filter(e -> e.type() == ExceptionType.SPECIAL_HOURS)
                .filter(e -> overlapsDay(e, date, request.zone()))
                .map(e -> new Interval(e.startsAt(), e.endsAt()))
                .collect(Collectors.toList());

        if (!specialHours.isEmpty()) {
            return specialHours;
        }

        int dayOfWeek = toDayOfWeekIndex(date);
        return request.blocks().stream()
                .filter(b -> b.dayOfWeek() == dayOfWeek)
                .map(b -> toInterval(date, b.startTime(), b.endTime(), request.zone()))
                .collect(Collectors.toList());
    }

    private static List<Interval> blockedIntervalsForDay(LocalDate date, AvailabilityRequest request) {
        return request.exceptions().stream()
                .filter(e -> e.type() == ExceptionType.BLOCK)
                .filter(e -> overlapsDay(e, date, request.zone()))
                .map(e -> new Interval(e.startsAt(), e.endsAt()))
                .collect(Collectors.toList());
    }

    private static boolean overlapsDay(ScheduleException exception, LocalDate date, ZoneId zone) {
        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();
        return exception.overlaps(dayStart, dayEnd);
    }

    private static Interval toInterval(LocalDate date, LocalTime start, LocalTime end, ZoneId zone) {
        ZonedDateTime zonedStart = ZonedDateTime.of(date, start, zone);
        ZonedDateTime zonedEnd = ZonedDateTime.of(date, end, zone);
        return new Interval(zonedStart.toInstant(), zonedEnd.toInstant());
    }

    /** 0 = domingo ... 6 = sabado, para que coincida con schedule_block.day_of_week. */
    private static int toDayOfWeekIndex(LocalDate date) {
        return date.getDayOfWeek().getValue() % 7;
    }

    private static List<Interval> subtractAll(Interval base, List<Interval> toSubtract) {
        List<Interval> remaining = new ArrayList<>(List.of(base));
        for (Interval blocked : toSubtract) {
            List<Interval> next = new ArrayList<>();
            for (Interval piece : remaining) {
                next.addAll(subtract(piece, blocked));
            }
            remaining = next;
        }
        return remaining;
    }

    private static List<Interval> subtract(Interval piece, Interval blocked) {
        if (blocked.end().compareTo(piece.start()) <= 0 || blocked.start().compareTo(piece.end()) >= 0) {
            return List.of(piece);
        }
        List<Interval> pieces = new ArrayList<>();
        if (blocked.start().isAfter(piece.start())) {
            pieces.add(new Interval(piece.start(), blocked.start()));
        }
        if (blocked.end().isBefore(piece.end())) {
            pieces.add(new Interval(blocked.end(), piece.end()));
        }
        return pieces;
    }
}
