package com.codefactory.bookingplatform.scheduling.api;

import com.codefactory.bookingplatform.scheduling.application.AddScheduleBlockUseCase;
import com.codefactory.bookingplatform.scheduling.application.ChangeScheduleGranularityUseCase;
import com.codefactory.bookingplatform.scheduling.application.CreateScheduleUseCase;
import com.codefactory.bookingplatform.scheduling.domain.model.Schedule;
import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleBlock;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
@PreAuthorize("hasAnyRole('PROVEEDOR', 'ADMINISTRADOR')")
public class ScheduleController {

    private final CreateScheduleUseCase createScheduleUseCase;
    private final AddScheduleBlockUseCase addScheduleBlockUseCase;
    private final ChangeScheduleGranularityUseCase changeScheduleGranularityUseCase;

    public ScheduleController(CreateScheduleUseCase createScheduleUseCase, AddScheduleBlockUseCase addScheduleBlockUseCase,
                               ChangeScheduleGranularityUseCase changeScheduleGranularityUseCase) {
        this.createScheduleUseCase = createScheduleUseCase;
        this.addScheduleBlockUseCase = addScheduleBlockUseCase;
        this.changeScheduleGranularityUseCase = changeScheduleGranularityUseCase;
    }

    public record CreateScheduleRequest(@NotNull UUID locationId, UUID resourceId, @NotNull Integer granularityMinutes,
                                         @NotNull LocalDate validFrom, LocalDate validTo) {}
    public record ScheduleResponse(UUID scheduleId) {}
    public record AddBlockRequest(@Min(0) @Max(6) int dayOfWeek, @NotNull LocalTime startTime, @NotNull LocalTime endTime) {}
    public record DuplicateDayRequest(@Min(0) @Max(6) int sourceDayOfWeek, @NotNull List<Integer> targetDaysOfWeek) {}
    public record GranularityRequest(@NotNull Integer granularityMinutes) {}

    @PostMapping
    public ResponseEntity<ScheduleResponse> create(@Valid @RequestBody CreateScheduleRequest request) {
        Schedule schedule = createScheduleUseCase.create(request.locationId(), request.resourceId(),
                request.granularityMinutes(), request.validFrom(), request.validTo());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ScheduleResponse(schedule.getId()));
    }

    @PostMapping("/{scheduleId}/blocks")
    public ResponseEntity<Void> addBlock(@PathVariable UUID scheduleId, @Valid @RequestBody AddBlockRequest request) {
        addScheduleBlockUseCase.addBlock(scheduleId, request.dayOfWeek(), request.startTime(), request.endTime());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{scheduleId}/blocks/duplicate")
    public ResponseEntity<List<ScheduleBlock>> duplicateDay(@PathVariable UUID scheduleId,
                                                             @Valid @RequestBody DuplicateDayRequest request) {
        return ResponseEntity.ok(addScheduleBlockUseCase.duplicateDay(scheduleId, request.sourceDayOfWeek(),
                request.targetDaysOfWeek()));
    }

    @PutMapping("/{scheduleId}/granularity")
    public ResponseEntity<Void> changeGranularity(@PathVariable UUID scheduleId, @Valid @RequestBody GranularityRequest request) {
        changeScheduleGranularityUseCase.change(scheduleId, request.granularityMinutes());
        return ResponseEntity.noContent().build();
    }
}
