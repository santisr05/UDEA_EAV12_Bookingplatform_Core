package com.codefactory.bookingplatform.scheduling.application;

import com.codefactory.bookingplatform.scheduling.domain.model.Schedule;
import com.codefactory.bookingplatform.scheduling.domain.port.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/** HU-006. */
@Service
public class CreateScheduleUseCase {

    private final ScheduleRepository scheduleRepository;

    public CreateScheduleUseCase(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public Schedule create(UUID locationId, UUID resourceId, int granularityMinutes, LocalDate validFrom, LocalDate validTo) {
        Schedule schedule = Schedule.create(UUID.randomUUID(), locationId, resourceId, granularityMinutes, validFrom, validTo);
        return scheduleRepository.save(schedule);
    }
}
