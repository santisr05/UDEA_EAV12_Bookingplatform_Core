package com.codefactory.bookingplatform.scheduling.application;

import com.codefactory.bookingplatform.scheduling.domain.model.Schedule;
import com.codefactory.bookingplatform.scheduling.domain.port.FutureBookingsQuery;
import com.codefactory.bookingplatform.scheduling.domain.port.ScheduleRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangeScheduleGranularityUseCase {

    private final ScheduleRepository scheduleRepository;
    private final FutureBookingsQuery futureBookingsQuery;

    public ChangeScheduleGranularityUseCase(ScheduleRepository scheduleRepository, FutureBookingsQuery futureBookingsQuery) {
        this.scheduleRepository = scheduleRepository;
        this.futureBookingsQuery = futureBookingsQuery;
    }

    @Transactional
    public void change(UUID scheduleId, int newGranularityMinutes) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> BusinessException.notFound("Agenda no encontrada"));
        if (futureBookingsQuery.countFutureConfirmedBookingsForSchedule(scheduleId) > 0) {
            throw BusinessException.invalidState("No se puede cambiar la granularidad: la agenda tiene reservas futuras");
        }
        schedule.changeGranularity(newGranularityMinutes);
        scheduleRepository.save(schedule);
    }
}
