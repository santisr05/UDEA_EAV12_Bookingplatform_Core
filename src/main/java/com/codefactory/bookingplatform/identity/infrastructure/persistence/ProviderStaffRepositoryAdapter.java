package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import com.codefactory.bookingplatform.identity.domain.model.ProviderOwner;
import com.codefactory.bookingplatform.identity.domain.model.ProviderStaff;
import com.codefactory.bookingplatform.identity.domain.port.ProviderStaffRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProviderStaffRepositoryAdapter implements ProviderStaffRepository {

    private final ProviderOwnerJpaRepository ownerJpaRepository;
    private final ProviderStaffJpaRepository staffJpaRepository;

    public ProviderStaffRepositoryAdapter(ProviderOwnerJpaRepository ownerJpaRepository,
                                           ProviderStaffJpaRepository staffJpaRepository) {
        this.ownerJpaRepository = ownerJpaRepository;
        this.staffJpaRepository = staffJpaRepository;
    }

    @Override
    public ProviderOwner saveOwner(ProviderOwner owner) {
        ownerJpaRepository.save(new ProviderOwnerJpaEntity(owner.id(), owner.userId(), owner.providerId()));
        return owner;
    }

    @Override
    public ProviderStaff saveStaff(ProviderStaff staff) {
        staffJpaRepository.save(new ProviderStaffJpaEntity(staff.id(), staff.userId(), staff.providerId(), staff.role()));
        return staff;
    }

    @Override
    public boolean isStaffOf(UUID userId, UUID providerId) {
        return staffJpaRepository.existsByUserIdAndProviderId(userId, providerId);
    }
}
