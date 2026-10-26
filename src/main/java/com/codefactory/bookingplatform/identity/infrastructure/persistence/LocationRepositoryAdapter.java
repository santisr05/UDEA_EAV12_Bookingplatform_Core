package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import com.codefactory.bookingplatform.identity.domain.model.Location;
import com.codefactory.bookingplatform.identity.domain.port.LocationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class LocationRepositoryAdapter implements LocationRepository {

    private final LocationJpaRepository jpaRepository;

    public LocationRepositoryAdapter(LocationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Location save(Location location) {
        jpaRepository.save(new LocationJpaEntity(location.getId(), location.getProviderId(), location.getName(),
                location.getAddress(), location.getCity(), location.getTimezone(), location.getStatus().name()));
        return location;
    }

    @Override
    public Optional<Location> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> Location.rehydrate(e.getId(), e.getProviderId(), e.getName(),
                e.getAddress(), e.getCity(), e.getTimezone(), Location.Status.valueOf(e.getStatus())));
    }

    @Override
    public List<Location> findByProviderId(UUID providerId) {
        return jpaRepository.findByProviderId(providerId).stream()
                .map(e -> Location.rehydrate(e.getId(), e.getProviderId(), e.getName(), e.getAddress(), e.getCity(),
                        e.getTimezone(), Location.Status.valueOf(e.getStatus())))
                .toList();
    }
}
