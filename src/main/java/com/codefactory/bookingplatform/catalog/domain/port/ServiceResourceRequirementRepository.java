package com.codefactory.bookingplatform.catalog.domain.port;

import com.codefactory.bookingplatform.catalog.domain.model.ServiceResourceRequirement;

import java.util.List;
import java.util.UUID;

public interface ServiceResourceRequirementRepository {
    ServiceResourceRequirement save(ServiceResourceRequirement requirement);
    List<ServiceResourceRequirement> findByServiceId(UUID serviceId);
}
