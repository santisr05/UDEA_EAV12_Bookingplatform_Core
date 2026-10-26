package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "schedule_block")
public class ScheduleBlockJpaEntity {
    @Id
    private UUID id;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    protected ScheduleBlockJpaEntity() {
    }

    public ScheduleBlockJpaEntity(UUID id, UUID scheduleId, int dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getId() { return id; }
    public UUID getScheduleId() { return scheduleId; }
    public int getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
}
