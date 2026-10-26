package com.codefactory.bookingplatform.scheduling.domain;

import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleBlock;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScheduleBlockTest {

    private final UUID scheduleId = UUID.randomUUID();

    @Test
    void rejects_end_before_or_equal_to_start() {
        assertThatThrownBy(() -> new ScheduleBlock(UUID.randomUUID(), scheduleId, 1,
                LocalTime.of(10, 0), LocalTime.of(10, 0)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void detects_overlap_same_day() {
        ScheduleBlock a = new ScheduleBlock(UUID.randomUUID(), scheduleId, 1, LocalTime.of(8, 0), LocalTime.of(12, 0));
        ScheduleBlock b = new ScheduleBlock(UUID.randomUUID(), scheduleId, 1, LocalTime.of(11, 0), LocalTime.of(14, 0));
        assertThat(a.overlaps(b)).isTrue();
    }

    @Test
    void no_overlap_different_days() {
        ScheduleBlock a = new ScheduleBlock(UUID.randomUUID(), scheduleId, 1, LocalTime.of(8, 0), LocalTime.of(12, 0));
        ScheduleBlock b = new ScheduleBlock(UUID.randomUUID(), scheduleId, 2, LocalTime.of(8, 0), LocalTime.of(12, 0));
        assertThat(a.overlaps(b)).isFalse();
    }

    @Test
    void no_overlap_when_adjacent() {
        ScheduleBlock a = new ScheduleBlock(UUID.randomUUID(), scheduleId, 1, LocalTime.of(8, 0), LocalTime.of(12, 0));
        ScheduleBlock b = new ScheduleBlock(UUID.randomUUID(), scheduleId, 1, LocalTime.of(12, 0), LocalTime.of(14, 0));
        assertThat(a.overlaps(b)).isFalse();
    }
}
