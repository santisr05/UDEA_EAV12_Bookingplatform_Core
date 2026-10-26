package com.codefactory.bookingplatform.scheduling.application;

import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleBlock;
import com.codefactory.bookingplatform.scheduling.domain.port.FutureBookingsQuery;
import com.codefactory.bookingplatform.scheduling.domain.port.ScheduleBlockRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * HU-006. Valida solapes contra los bloques ya existentes del mismo dia, y
 * bloquea el cambio si hay reservas futuras confirmadas que quedarian fuera
 * del nuevo horario (simplificado: se bloquea el cambio en bloque mientras
 * existan reservas futuras confirmadas sobre esta agenda; resolverlas antes
 * es responsabilidad del proveedor, igual que exige el escenario de HU-006).
 */
@Service
public class AddScheduleBlockUseCase {

    private final ScheduleBlockRepository scheduleBlockRepository;
    private final FutureBookingsQuery futureBookingsQuery;

    public AddScheduleBlockUseCase(ScheduleBlockRepository scheduleBlockRepository,
                                    FutureBookingsQuery futureBookingsQuery) {
        this.scheduleBlockRepository = scheduleBlockRepository;
        this.futureBookingsQuery = futureBookingsQuery;
    }

    @Transactional
    public ScheduleBlock addBlock(UUID scheduleId, int dayOfWeek, LocalTime startTime, LocalTime endTime) {
        ScheduleBlock candidate = new ScheduleBlock(UUID.randomUUID(), scheduleId, dayOfWeek, startTime, endTime);

        List<ScheduleBlock> existing = scheduleBlockRepository.findByScheduleId(scheduleId);
        boolean overlaps = existing.stream().anyMatch(candidate::overlaps);
        if (overlaps) {
            throw BusinessException.rule("El bloque se solapa con otro bloque existente el mismo dia");
        }

        if (!existing.isEmpty() && futureBookingsQuery.countFutureConfirmedBookingsForSchedule(scheduleId) > 0) {
            throw BusinessException.invalidState(
                    "Hay reservas futuras confirmadas dentro del horario actual; resuelvalas antes de modificar la agenda");
        }

        return scheduleBlockRepository.save(candidate);
    }

    /** HU-006, "Duplicar la configuracion de un dia". */
    @Transactional
    public List<ScheduleBlock> duplicateDay(UUID scheduleId, int sourceDayOfWeek, List<Integer> targetDaysOfWeek) {
        List<ScheduleBlock> sourceBlocks = scheduleBlockRepository.findByScheduleId(scheduleId).stream()
                .filter(b -> b.dayOfWeek() == sourceDayOfWeek)
                .toList();
        return targetDaysOfWeek.stream()
                .flatMap(day -> sourceBlocks.stream()
                        .map(b -> addBlock(scheduleId, day, b.startTime(), b.endTime())))
                .toList();
    }
}
