package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import com.codefactory.bookingplatform.catalog.domain.model.Currency;
import com.codefactory.bookingplatform.catalog.domain.port.CurrencyRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CurrencyRepositoryAdapter implements CurrencyRepository {

    private final CurrencyJpaRepository jpaRepository;

    public CurrencyRepositoryAdapter(CurrencyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        return jpaRepository.findByCode(code).map(e -> new Currency(e.getId(), e.getName(), e.getCode()));
    }

    @Override
    public Optional<Currency> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> new Currency(e.getId(), e.getName(), e.getCode()));
    }
}
