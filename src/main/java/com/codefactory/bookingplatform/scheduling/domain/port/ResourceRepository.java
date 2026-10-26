package com.codefactory.bookingplatform.scheduling.domain.port;

import com.codefactory.bookingplatform.scheduling.domain.model.Resource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourceRepository {
    Resource save(Resource resource);
    Optional<Resource> findById(UUID id);
    List<Resource> findByResourceTypeId(UUID resourceTypeId);
    boolean existsAvailableByResourceTypeId(UUID resourceTypeId);
}
