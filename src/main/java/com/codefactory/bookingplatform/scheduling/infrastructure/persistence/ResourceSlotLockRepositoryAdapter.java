package com.codefactory.bookingplatform.scheduling.infrastructure.persistence;

import com.codefactory.bookingplatform.scheduling.domain.port.ResourceSlotLockRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ResourceSlotLockRepositoryAdapter implements ResourceSlotLockRepository {

    private final ResourceSlotLockJpaRepository jpaRepository;

    public ResourceSlotLockRepositoryAdapter(ResourceSlotLockJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void lockSlots(UUID resourceId, UUID bookingId, List<Instant> slotStarts) {
        // Un solo saveAll: si CUALQUIER fila viola el UNIQUE(resource_id, slot_start),
        // todo el batch falla y la transaccion REQUIRES_NEW que lo envuelve se revierte.
        List<ResourceSlotLockJpaEntity> entities = slotStarts.stream()
                .map(slot -> new ResourceSlotLockJpaEntity(UUID.randomUUID(), resourceId, bookingId, slot))
                .toList();
        jpaRepository.saveAll(entities);
        jpaRepository.flush();
    }

    @Override
    public void releaseSlots(UUID bookingId) {
        jpaRepository.deleteByBookingId(bookingId);
    }

    @Override
    public boolean hasAnyLockInRange(UUID resourceId, Instant start, Instant end) {
        return jpaRepository.existsByResourceIdAndSlotStartBetween(resourceId, start, end);
    }
}
