package com.codefactory.bookingplatform.identity.application;

import com.codefactory.bookingplatform.identity.domain.model.Provider;
import com.codefactory.bookingplatform.identity.domain.port.FutureConfirmedBookingsQuery;
import com.codefactory.bookingplatform.identity.domain.port.ProviderRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * HU-003, escenario "Suspender un proveedor con reservas futuras": si hay
 * reservas afectadas y el llamador todavia no confirmo (force=false), se
 * responde con el conteo para que el cliente (UI) pida confirmacion antes
 * de reintentar con force=true. El motivo es obligatorio en ambos casos.
 */
@Service
public class SuspendProviderUseCase {

    private final ProviderRepository providerRepository;
    private final FutureConfirmedBookingsQuery futureConfirmedBookingsQuery;

    public SuspendProviderUseCase(ProviderRepository providerRepository,
                                   FutureConfirmedBookingsQuery futureConfirmedBookingsQuery) {
        this.providerRepository = providerRepository;
        this.futureConfirmedBookingsQuery = futureConfirmedBookingsQuery;
    }

    public record Result(long affectedBookings, boolean suspended) {}

    @Transactional
    public Result suspend(UUID providerId, String reason, boolean force) {
        if (reason == null || reason.isBlank()) {
            throw BusinessException.rule("El motivo de suspension es obligatorio");
        }
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> BusinessException.notFound("Proveedor no encontrado"));

        long affected = futureConfirmedBookingsQuery.countFutureConfirmedBookings(providerId);
        if (affected > 0 && !force) {
            return new Result(affected, false);
        }

        provider.suspend();
        providerRepository.save(provider);
        return new Result(affected, true);
    }
}
