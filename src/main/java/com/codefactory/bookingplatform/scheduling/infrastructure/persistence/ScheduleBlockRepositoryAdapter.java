package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleBlock;
import com.codefactory.bookingplatform.scheduling.domain.port.ScheduleBlockRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ScheduleBlockRepositoryAdapter implements ScheduleBlockRepository {

    private final ScheduleBlockJpaRepository jpaRepository;

    public ScheduleBlockRepositoryAdapter(ScheduleBlockJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ScheduleBlock save(ScheduleBlock block) {
        jpaRepository.save(new ScheduleBlockJpaEntity(block.id(), block.scheduleId(), block.dayOfWeek(),
                block.startTime(), block.endTime()));
        return block;
    }

    @Override
    public List<ScheduleBlock> findByScheduleId(UUID scheduleId) {
        return jpaRepository.findByScheduleId(scheduleId).stream()
                .map(e -> new ScheduleBlock(e.getId(), e.getScheduleId(), e.getDayOfWeek(), e.getStartTime(), e.getEndTime()))
                .toList();
    }
}
