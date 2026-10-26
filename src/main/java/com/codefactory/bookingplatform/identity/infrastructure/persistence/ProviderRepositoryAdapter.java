package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import com.codefactory.bookingplatform.identity.domain.model.Provider;
import com.codefactory.bookingplatform.identity.domain.model.ProviderStatus;
import com.codefactory.bookingplatform.identity.domain.port.ProviderRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProviderRepositoryAdapter implements ProviderRepository {

    private final ProviderJpaRepository jpaRepository;

    public ProviderRepositoryAdapter(ProviderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Provider save(Provider provider) {
        ProviderJpaEntity entity = new ProviderJpaEntity(provider.getId(), provider.getUserId(),
                provider.getLegalName(), provider.getNit(), provider.getCategory(), provider.getContactEmail(),
                provider.getContactPhone(), provider.getTimezone(), provider.getStatus().name(),
                provider.getApprovedAt(), provider.getMinBookingNoticeMinutes(), provider.getMaxBookingNoticeMinutes(),
                provider.getFreeCancellationWindowMinutes());
        jpaRepository.save(entity);
        return provider;
    }

    @Override
    public Optional<Provider> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> Provider.rehydrate(e.getId(), e.getUserId(), e.getLegalName(),
                e.getNit(), e.getCategory(), e.getContactEmail(), e.getContactPhone(), e.getTimezone(),
                ProviderStatus.valueOf(e.getStatus()), e.getApprovedAt(), e.getMinBookingNoticeMinutes(),
                e.getMaxBookingNoticeMinutes(), e.getFreeCancellationWindowMinutes()));
    }

    @Override
    public boolean existsByNit(String nit) {
        return jpaRepository.existsByNit(nit);
    }
}
