package com.codefactory.bookingplatform.identity.application;

import com.codefactory.bookingplatform.identity.infrastructure.persistence.ClientJpaEntity;
import com.codefactory.bookingplatform.identity.infrastructure.persistence.ClientJpaRepository;
import com.codefactory.bookingplatform.identity.infrastructure.persistence.ProviderJpaEntity;
import com.codefactory.bookingplatform.identity.infrastructure.persistence.ProviderJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * HU-003. Listado administrativo con filtros combinables. Al tratarse de una
 * consulta de solo lectura orientada a presentacion, se apoya directamente en
 * los repositorios JPA (lado de lectura) en vez de pasar por el puerto de
 * dominio, que esta pensado para el ciclo de vida de escritura del agregado.
 */
@Service
public class ListUsersUseCase {

    private final ClientJpaRepository clientJpaRepository;
    private final ProviderJpaRepository providerJpaRepository;

    public ListUsersUseCase(ClientJpaRepository clientJpaRepository, ProviderJpaRepository providerJpaRepository) {
        this.clientJpaRepository = clientJpaRepository;
        this.providerJpaRepository = providerJpaRepository;
    }

    public Page<ClientJpaEntity> searchClients(String status, String city, Instant from, Instant to, Pageable pageable) {
        Specification<ClientJpaEntity> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (city != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("city"), city));
        }
        if (from != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from));
        }
        if (to != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to));
        }
        return clientJpaRepository.findAll(spec, pageable);
    }

    public Page<ProviderJpaEntity> searchProviders(String status, String category, Pageable pageable) {
        Specification<ProviderJpaEntity> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (category != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("category"), category));
        }
        return providerJpaRepository.findAll(spec, pageable);
    }
}
