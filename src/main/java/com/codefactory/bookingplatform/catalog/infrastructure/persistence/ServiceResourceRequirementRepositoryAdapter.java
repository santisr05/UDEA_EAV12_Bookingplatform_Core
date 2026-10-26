package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import com.codefactory.bookingplatform.catalog.domain.model.ServiceResourceRequirement;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceResourceRequirementRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ServiceResourceRequirementRepositoryAdapter implements ServiceResourceRequirementRepository {

    private final ServiceResourceRequirementJpaRepository jpaRepository;

    public ServiceResourceRequirementRepositoryAdapter(ServiceResourceRequirementJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceResourceRequirement save(ServiceResourceRequirement requirement) {
        jpaRepository.save(new ServiceResourceRequirementJpaEntity(requirement.id(), requirement.serviceId(),
                requirement.resourceTypeId(), requirement.quantityRequired()));
        return requirement;
    }

    @Override
    public List<ServiceResourceRequirement> findByServiceId(UUID serviceId) {
        return jpaRepository.findByServiceId(serviceId).stream()
                .map(e -> new ServiceResourceRequirement(e.getId(), e.getServiceId(), e.getResourceTypeId(), e.getQuantityRequired()))
                .toList();
    }
}
