package com.codefactory.bookingplatform.scheduling.api;

import com.codefactory.bookingplatform.scheduling.application.CreateScheduleExceptionUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules/{scheduleId}/exceptions")
@PreAuthorize("hasAnyRole('PROVEEDOR', 'ADMINISTRADOR')")
public class ScheduleExceptionController {

    private final CreateScheduleExceptionUseCase createScheduleExceptionUseCase;

    public ScheduleExceptionController(CreateScheduleExceptionUseCase createScheduleExceptionUseCase) {
        this.createScheduleExceptionUseCase = createScheduleExceptionUseCase;
    }

    public record BlockRequest(@NotNull Instant startsAt, @NotNull Instant endsAt, String reason, boolean recurring,
                                boolean force) {}
    public record SpecialHoursRequest(@NotNull Instant startsAt, @NotNull Instant endsAt, String reason) {}
    public record HolidaysRequest(@NotNull List<LocalDate> holidays, @NotNull String zoneId) {}
    public record BlockResponse(long affectedBookings, boolean created) {}

    @PostMapping("/blocks")
    public ResponseEntity<BlockResponse> createBlock(@PathVariable UUID scheduleId, @Valid @RequestBody BlockRequest request) {
        var result = createScheduleExceptionUseCase.createBlock(scheduleId, request.startsAt(), request.endsAt(),
                request.reason(), request.recurring(), request.force());
        return ResponseEntity.ok(new BlockResponse(result.affectedBookings(), result.created() != null));
    }

    @PostMapping("/special-hours")
    public ResponseEntity<Void> createSpecialHours(@PathVariable UUID scheduleId, @Valid @RequestBody SpecialHoursRequest request) {
        createScheduleExceptionUseCase.createSpecialHours(scheduleId, request.startsAt(), request.endsAt(), request.reason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/holidays")
    public ResponseEntity<Void> loadHolidays(@PathVariable UUID scheduleId, @Valid @RequestBody HolidaysRequest request) {
        createScheduleExceptionUseCase.loadHolidays(scheduleId, request.holidays(), ZoneId.of(request.zoneId()));
        return ResponseEntity.noContent().build();
    }
}
