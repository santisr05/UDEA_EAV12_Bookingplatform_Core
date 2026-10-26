package com.codefactory.bookingplatform.identity.application;

import com.codefactory.bookingplatform.identity.domain.model.Provider;
import com.codefactory.bookingplatform.identity.domain.port.ProviderRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateOperationPolicyUseCase {

    private final ProviderRepository providerRepository;

    public UpdateOperationPolicyUseCase(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional
    public void update(UUID providerId, int minNoticeMinutes, int maxNoticeMinutes, int freeCancellationWindowMinutes) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> BusinessException.notFound("Proveedor no encontrado"));
        provider.updateOperationPolicy(minNoticeMinutes, maxNoticeMinutes, freeCancellationWindowMinutes);
        providerRepository.save(provider);
    }
}
