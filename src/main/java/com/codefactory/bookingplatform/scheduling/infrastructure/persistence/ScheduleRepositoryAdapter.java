package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import com.codefactory.bookingplatform.scheduling.domain.model.Schedule;
import com.codefactory.bookingplatform.scheduling.domain.port.ScheduleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ScheduleRepositoryAdapter implements ScheduleRepository {

    private final ScheduleJpaRepository jpaRepository;

    public ScheduleRepositoryAdapter(ScheduleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Schedule save(Schedule schedule) {
        jpaRepository.save(new ScheduleJpaEntity(schedule.getId(), schedule.getLocationId(), schedule.getResourceId(),
                schedule.getGranularityMinutes(), schedule.getValidFrom(), schedule.getValidTo(),
                schedule.getStatus().name()));
        return schedule;
    }

    @Override
    public Optional<Schedule> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> Schedule.rehydrate(e.getId(), e.getLocationId(), e.getResourceId(),
                e.getGranularityMinutes(), e.getValidFrom(), e.getValidTo(), Schedule.Status.valueOf(e.getStatus())));
    }
}
