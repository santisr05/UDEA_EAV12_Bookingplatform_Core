package com.codefactory.bookingplatform.identity.domain.port;

import com.codefactory.bookingplatform.identity.domain.model.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository {
    Location save(Location location);
    Optional<Location> findById(UUID id);
    List<Location> findByProviderId(UUID providerId);
}
