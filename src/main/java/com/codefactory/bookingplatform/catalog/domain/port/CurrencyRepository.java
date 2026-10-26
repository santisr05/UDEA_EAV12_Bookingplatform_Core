package com.codefactory.bookingplatform.catalog.domain.port;

import com.codefactory.bookingplatform.catalog.domain.model.Currency;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyRepository {
    Optional<Currency> findByCode(String code);
    Optional<Currency> findById(UUID id);
}
