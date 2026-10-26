package com.codefactory.bookingplatform.identity.domain.port;

import com.codefactory.bookingplatform.identity.domain.model.Provider;

import java.util.Optional;
import java.util.UUID;

public interface ProviderRepository {
    Provider save(Provider provider);
    Optional<Provider> findById(UUID id);
    boolean existsByNit(String nit);
}
