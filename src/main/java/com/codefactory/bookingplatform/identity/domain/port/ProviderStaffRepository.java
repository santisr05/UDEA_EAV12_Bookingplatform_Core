package com.codefactory.bookingplatform.identity.domain.port;

import com.codefactory.bookingplatform.identity.domain.model.ProviderOwner;
import com.codefactory.bookingplatform.identity.domain.model.ProviderStaff;

import java.util.UUID;

public interface ProviderStaffRepository {
    ProviderOwner saveOwner(ProviderOwner owner);
    ProviderStaff saveStaff(ProviderStaff staff);
    boolean isStaffOf(UUID userId, UUID providerId);
}
