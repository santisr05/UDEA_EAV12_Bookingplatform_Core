package com.codefactory.bookingplatform.scheduling.application;

import com.codefactory.bookingplatform.scheduling.domain.model.Resource;
import com.codefactory.bookingplatform.scheduling.domain.model.ResourceStatus;
import com.codefactory.bookingplatform.scheduling.domain.port.ResourceRepository;
import com.codefactory.bookingplatform.scheduling.domain.port.ResourceSlotLockRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * HU-010. El nucleo de la prevencion de doble agendamiento (NN-03) en MySQL:
 * cada candidato se intenta en su PROPIA transaccion (REQUIRES_NEW), de modo
 * que si su insercion en resource_slot_lock falla por clave duplicada
 * (alguien mas tomo ese recurso en ese instante), esa transaccion se revierte
 * sola y se intenta el siguiente candidato, sin afectar lo que ya se asigno
 * para otros tipos de recurso en la misma reserva.
 */
@Service
public class ReserveResourcesUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceSlotLockRepository resourceSlotLockRepository;
    private final TransactionTemplate requiresNewTransactionTemplate;

    public ReserveResourcesUseCase(ResourceRepository resourceRepository,
                                    ResourceSlotLockRepository resourceSlotLockRepository,
                                    PlatformTransactionManager transactionManager) {
        this.resourceRepository = resourceRepository;
        this.resourceSlotLockRepository = resourceSlotLockRepository;
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.requiresNewTransactionTemplate = new TransactionTemplate(transactionManager, definition);
    }

    public record ResourceRequirement(UUID resourceTypeId, int quantity) {}

    /**
     * @return los recursos efectivamente asignados. Si no alcanza para
     * cumplir TODOS los requerimientos, libera lo que alcanzo a asignar y
     * lanza BusinessException (HU-010, "Rechazo por falta de recursos").
     */
    public List<UUID> reserve(UUID bookingId, List<ResourceRequirement> requirements, Instant startsAt, Instant endsAt,
                               int granularityMinutes) {
        List<Instant> slotStarts = computeSlotStarts(startsAt, endsAt, granularityMinutes);
        List<UUID> assigned = new ArrayList<>();

        for (ResourceRequirement requirement : requirements) {
            List<Resource> candidates = resourceRepository.findByResourceTypeId(requirement.resourceTypeId()).stream()
                    .filter(r -> r.getStatus() == ResourceStatus.AVAILABLE)
                    .toList();

            int lockedForThisType = 0;
            for (Resource candidate : candidates) {
                if (lockedForThisType >= requirement.quantity()) {
                    break;
                }
                boolean locked = tryLock(candidate.getId(), bookingId, slotStarts);
                if (locked) {
                    assigned.add(candidate.getId());
                    lockedForThisType++;
                }
            }

            if (lockedForThisType < requirement.quantity()) {
                releaseAll(bookingId);
                throw BusinessException.unavailable(
                        "No hay recursos suficientes disponibles para el horario solicitado");
            }
        }
        return assigned;
    }

    public void release(UUID bookingId) {
        releaseAll(bookingId);
    }

    private boolean tryLock(UUID resourceId, UUID bookingId, List<Instant> slotStarts) {
        try {
            Boolean result = requiresNewTransactionTemplate.execute(status ->
                    tryLockInternal(resourceId, bookingId, slotStarts));
            return Boolean.TRUE.equals(result);
        } catch (DataIntegrityViolationException ex) {
            return false;
        }
    }

    private boolean tryLockInternal(UUID resourceId, UUID bookingId, List<Instant> slotStarts) {
        resourceSlotLockRepository.lockSlots(resourceId, bookingId, slotStarts);
        return true;
    }

    private void releaseAll(UUID bookingId) {
        requiresNewTransactionTemplate.executeWithoutResult(status -> resourceSlotLockRepository.releaseSlots(bookingId));
    }

    private List<Instant> computeSlotStarts(Instant startsAt, Instant endsAt, int granularityMinutes) {
        List<Instant> slots = new ArrayList<>();
        Duration step = Duration.ofMinutes(granularityMinutes);
        for (Instant slot = startsAt; slot.isBefore(endsAt); slot = slot.plus(step)) {
            slots.add(slot);
        }
        return slots;
    }
}
