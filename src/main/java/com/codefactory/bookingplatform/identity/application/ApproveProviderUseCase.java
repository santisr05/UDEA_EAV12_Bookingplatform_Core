package com.codefactory.bookingplatform.identity.application;

import com.codefactory.bookingplatform.identity.domain.model.Provider;
import com.codefactory.bookingplatform.identity.domain.port.ProviderRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApproveProviderUseCase {

    private final ProviderRepository providerRepository;

    public ApproveProviderUseCase(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional
    public void approve(UUID providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> BusinessException.notFound("Proveedor no encontrado"));
        provider.approve();
        providerRepository.save(provider);
        // El envio del correo de notificacion de aprobacion lo maneja el
        // modulo de notificaciones, fuera del alcance de HU-002.
    }
}
