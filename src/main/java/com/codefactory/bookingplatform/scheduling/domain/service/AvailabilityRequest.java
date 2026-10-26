package com.codefactory.bookingplatform.scheduling.domain.service;

import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleBlock;
import com.codefactory.bookingplatform.scheduling.domain.model.ScheduleException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @param hasFreeResource predicado inyectado por la capa de aplicacion:
 *                         responde si existe al menos un recurso libre para
 *                         el rango [inicio, fin). Mantiene AvailabilityCalculator
 *                         puro (sin JPA) a la vez que puede consultar estado real.
 */
public record AvailabilityRequest(
        List<ScheduleBlock> blocks,
        List<ScheduleException> exceptions,
        int slotDurationMinutes,
        int granularityMinutes,
        LocalDate rangeStart,
        LocalDate rangeEnd,
        ZoneId zone,
        int minNoticeMinutes,
        int maxNoticeMinutes,
        Instant now,
        BiPredicate<Instant, Instant> hasFreeResource
) {}
