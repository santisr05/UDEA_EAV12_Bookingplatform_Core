package com.codefactory.bookingplatform.scheduling.domain.port;

import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleBlock;

import java.util.List;
import java.util.UUID;

public interface ScheduleBlockRepository {
    ScheduleBlock save(ScheduleBlock block);
    List<ScheduleBlock> findByScheduleId(UUID scheduleId);
}
