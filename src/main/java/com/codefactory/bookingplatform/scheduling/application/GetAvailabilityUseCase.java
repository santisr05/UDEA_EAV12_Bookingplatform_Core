package com.codefactory.bookingplatform.scheduling.application;

import com.codefactory.bookingplatform.scheduling.domain.model.Resource;
import com.codefactory.bookingplatform.scheduling.domain.model.ResourceStatus;
import com.codefactory.bookingplatform.scheduling.domain.model.Schedule;
import com.codefactory.bookingplatform.scheduling.domain.port.*;
import com.codefactory.bookingplatform.scheduling.domain.service.AvailabilityCalculator;
import com.codefactory.bookingplatform.scheduling.domain.service.AvailabilityRequest;
import com.codefactory.bookingplatform.scheduling.domain.service.AvailableSlot;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * HU-008 (calculo) y HU-011 (consulta por el cliente). Un recurso se
 * considera "libre" para un candidato si, para CADA tipo de recurso
 * requerido, existe al menos una instancia de ese tipo sin locks en el rango
 * — es una aproximacion por disponibilidad de tipo, no una pre-asignacion
 * (la asignacion real y definitiva ocurre en ReserveResourcesUseCase, que es
 * quien de verdad compite por el recurso especifico via UNIQUE KEY).
 */
@Service
public class GetAvailabilityUseCase {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleBlockRepository scheduleBlockRepository;
    private final ScheduleExceptionRepository scheduleExceptionRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceSlotLockRepository resourceSlotLockRepository;

    public GetAvailabilityUseCase(ScheduleRepository scheduleRepository, ScheduleBlockRepository scheduleBlockRepository,
                                   ScheduleExceptionRepository scheduleExceptionRepository,
                                   ResourceRepository resourceRepository,
                                   ResourceSlotLockRepository resourceSlotLockRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleBlockRepository = scheduleBlockRepository;
        this.scheduleExceptionRepository = scheduleExceptionRepository;
        this.resourceRepository = resourceRepository;
        this.resourceSlotLockRepository = resourceSlotLockRepository;
    }

    public record RequiredResourceType(UUID resourceTypeId, int quantity) {}

    public List<AvailableSlot> getAvailability(UUID scheduleId, int serviceDurationMinutes,
                                                List<RequiredResourceType> requiredResourceTypes,
                                                int minNoticeMinutes, int maxNoticeMinutes,
                                                LocalDate rangeStart, LocalDate rangeEnd, ZoneId zone) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> BusinessException.notFound("Agenda no encontrada"));

        var blocks = scheduleBlockRepository.findByScheduleId(scheduleId);
        Instant rangeStartInstant = rangeStart.atStartOfDay(zone).toInstant();
        Instant rangeEndInstant = rangeEnd.plusDays(1).atStartOfDay(zone).toInstant();
        var exceptions = scheduleExceptionRepository.findByScheduleIdAndRange(scheduleId, rangeStartInstant, rangeEndInstant);

        Map<UUID, List<Resource>> instancesByType = requiredResourceTypes.stream()
                .collect(java.util.stream.Collectors.toMap(
                        RequiredResourceType::resourceTypeId,
                        rt -> resourceRepository.findByResourceTypeId(rt.resourceTypeId()).stream()
                                .filter(r -> r.getStatus() == ResourceStatus.AVAILABLE)
                                .toList()));

        AvailabilityRequest request = new AvailabilityRequest(
                blocks, exceptions, serviceDurationMinutes, schedule.getGranularityMinutes(),
                rangeStart, rangeEnd, zone, minNoticeMinutes, maxNoticeMinutes, Instant.now(),
                (start, end) -> requiredResourceTypes.stream().allMatch(rt -> {
                    long freeCount = instancesByType.get(rt.resourceTypeId()).stream()
                            .filter(resource -> !resourceSlotLockRepository.hasAnyLockInRange(resource.getId(), start, end))
                            .count();
                    return freeCount >= rt.quantity();
                })
        );

        return AvailabilityCalculator.calculate(request);
    }
}
