package com.codefactory.bookingplatform.scheduling.domain;

import com.codefactory.bookingplatform.scheduling.domain.model.ExceptionType;
import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleBlock;
import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleException;
import com.codefactory.bookingplatform.scheduling.domain.service.AvailabilityCalculator;
import com.codefactory.bookingplatform.scheduling.domain.service.AvailabilityRequest;
import com.codefactory.bookingplatform.scheduling.domain.service.AvailableSlot;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityCalculatorTest {

    private final UUID scheduleId = UUID.randomUUID();
    private final ZoneId zone = ZoneId.of("America/Bogota");
    // Jueves 2026-10-15 (coincide con la fecha "actual" del escenario del curso)
    private final LocalDate thursday = LocalDate.of(2026, 10, 15);

    private ScheduleBlock blockFor(LocalDate date, LocalTime start, LocalTime end) {
        int dayOfWeek = date.getDayOfWeek().getValue() % 7;
        return new ScheduleBlock(UUID.randomUUID(), scheduleId, dayOfWeek, start, end);
    }

    @Test
    void generates_slots_within_the_regular_block() {
        List<ScheduleBlock> blocks = List.of(blockFor(thursday, LocalTime.of(8, 0), LocalTime.of(10, 0)));
        AvailabilityRequest request = new AvailabilityRequest(blocks, List.of(), 60, 60, thursday, thursday, zone,
                0, 43200, thursday.atStartOfDay(zone).toInstant(), (s, e) -> true);

        List<AvailableSlot> slots = AvailabilityCalculator.calculate(request);

        assertThat(slots).hasSize(2); // 8-9 y 9-10
    }

    @Test
    void no_slot_offered_when_no_free_resource() {
        List<ScheduleBlock> blocks = List.of(blockFor(thursday, LocalTime.of(8, 0), LocalTime.of(10, 0)));
        AvailabilityRequest request = new AvailabilityRequest(blocks, List.of(), 60, 60, thursday, thursday, zone,
                0, 43200, thursday.atStartOfDay(zone).toInstant(), (s, e) -> false);

        assertThat(AvailabilityCalculator.calculate(request)).isEmpty();
    }

    @Test
    void block_exception_removes_overlapping_slots() {
        List<ScheduleBlock> blocks = List.of(blockFor(thursday, LocalTime.of(8, 0), LocalTime.of(12, 0)));
        Instant blockStart = ZonedDateTime.of(thursday, LocalTime.of(9, 0), zone).toInstant();
        Instant blockEnd = ZonedDateTime.of(thursday, LocalTime.of(11, 0), zone).toInstant();
        List<ScheduleException> exceptions = List.of(
                new ScheduleException(UUID.randomUUID(), scheduleId, ExceptionType.BLOCK, blockStart, blockEnd, "Mantenimiento", false));

        AvailabilityRequest request = new AvailabilityRequest(blocks, exceptions, 60, 60, thursday, thursday, zone,
                0, 43200, thursday.atStartOfDay(zone).toInstant(), (s, e) -> true);

        List<AvailableSlot> slots = AvailabilityCalculator.calculate(request);

        // Horario regular 8-12 menos el bloqueo 9-11 deja libres 8-9 y 11-12
        assertThat(slots).hasSize(2);
        assertThat(slots).extracting(AvailableSlot::startsAt)
                .containsExactlyInAnyOrder(
                        ZonedDateTime.of(thursday, LocalTime.of(8, 0), zone).toInstant(),
                        ZonedDateTime.of(thursday, LocalTime.of(11, 0), zone).toInstant());
    }

    @Test
    void special_hours_exception_replaces_regular_block_for_that_day() {
        List<ScheduleBlock> blocks = List.of(blockFor(thursday, LocalTime.of(8, 0), LocalTime.of(18, 0)));
        Instant specialStart = ZonedDateTime.of(thursday, LocalTime.of(10, 0), zone).toInstant();
        Instant specialEnd = ZonedDateTime.of(thursday, LocalTime.of(12, 0), zone).toInstant();
        List<ScheduleException> exceptions = List.of(
                new ScheduleException(UUID.randomUUID(), scheduleId, ExceptionType.SPECIAL_HOURS, specialStart, specialEnd, "Horario especial", false));

        AvailabilityRequest request = new AvailabilityRequest(blocks, exceptions, 60, 60, thursday, thursday, zone,
                0, 43200, thursday.atStartOfDay(zone).toInstant(), (s, e) -> true);

        List<AvailableSlot> slots = AvailabilityCalculator.calculate(request);

        assertThat(slots).hasSize(2); // solo 10-11 y 11-12, el resto del dia se ignora
    }

    @Test
    void respects_minimum_notice() {
        List<ScheduleBlock> blocks = List.of(blockFor(thursday, LocalTime.of(0, 0), LocalTime.of(23, 0)));
        Instant now = ZonedDateTime.of(thursday, LocalTime.of(8, 0), zone).toInstant();

        AvailabilityRequest request = new AvailabilityRequest(blocks, List.of(), 60, 60, thursday, thursday, zone,
                120, 43200, now, (s, e) -> true); // 2 horas de anticipacion minima

        List<AvailableSlot> slots = AvailabilityCalculator.calculate(request);

        Instant earliestExpected = now.plus(Duration.ofMinutes(120));
        assertThat(slots).allMatch(slot -> !slot.startsAt().isBefore(earliestExpected));
    }

    @Test
    void empty_range_without_data_returns_empty_list_not_error() {
        AvailabilityRequest request = new AvailabilityRequest(List.of(), List.of(), 60, 60, thursday, thursday, zone,
                0, 43200, thursday.atStartOfDay(zone).toInstant(), (s, e) -> true);

        assertThat(AvailabilityCalculator.calculate(request)).isEmpty();
    }
}
