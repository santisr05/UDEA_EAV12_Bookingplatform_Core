package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import com.codefactory.bookingplatform.scheduling.domain.model.ExceptionType;
import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleException;
import com.codefactory.bookingplatform.scheduling.domain.port.ScheduleExceptionRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ScheduleExceptionRepositoryAdapter implements ScheduleExceptionRepository {

    private final ScheduleExceptionJpaRepository jpaRepository;

    public ScheduleExceptionRepositoryAdapter(ScheduleExceptionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ScheduleException save(ScheduleException exception) {
        jpaRepository.save(new ScheduleExceptionJpaEntity(exception.id(), exception.scheduleId(),
                exception.type().name(), exception.startsAt(), exception.endsAt(), exception.reason(),
                exception.recurring()));
        return exception;
    }

    @Override
    public List<ScheduleException> findByScheduleIdAndRange(UUID scheduleId, Instant from, Instant to) {
        return jpaRepository.findByScheduleIdAndRange(scheduleId, from, to).stream()
                .map(e -> new ScheduleException(e.getId(), e.getScheduleId(), ExceptionType.valueOf(e.getType()),
                        e.getStartsAt(), e.getEndsAt(), e.getReason(), e.isRecurring()))
                .toList();
    }
}
