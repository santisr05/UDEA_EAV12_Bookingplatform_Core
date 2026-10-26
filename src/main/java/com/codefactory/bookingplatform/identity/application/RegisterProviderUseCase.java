package com.codefactory.bookingplatform.identity.application;

import com.codefactory.bookingplatform.auth.domain.port.UserProvisioning;
import com.codefactory.bookingplatform.identity.application.dto.RegisterProviderCommand;
import com.codefactory.bookingplatform.identity.domain.model.Location;
import com.codefactory.bookingplatform.identity.domain.model.Provider;
import com.codefactory.bookingplatform.identity.domain.model.ProviderOwner;
import com.codefactory.bookingplatform.identity.domain.port.LocationRepository;
import com.codefactory.bookingplatform.identity.domain.port.ProviderRepository;
import com.codefactory.bookingplatform.identity.domain.port.ProviderStaffRepository;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * HU-002. Exige al menos una sede en el mismo acto de registro (escenario
 * "Registro de un proveedor con al menos una sede").
 */
@Service
public class RegisterProviderUseCase {

    private final ProviderRepository providerRepository;
    private final ProviderStaffRepository providerStaffRepository;
    private final LocationRepository locationRepository;
    private final UserProvisioning userProvisioning;

    public RegisterProviderUseCase(ProviderRepository providerRepository,
                                    ProviderStaffRepository providerStaffRepository,
                                    LocationRepository locationRepository,
                                    UserProvisioning userProvisioning) {
        this.providerRepository = providerRepository;
        this.providerStaffRepository = providerStaffRepository;
        this.locationRepository = locationRepository;
        this.userProvisioning = userProvisioning;
    }

    @Transactional
    public Provider register(RegisterProviderCommand command) {
        if (providerRepository.existsByNit(command.nit())) {
            throw BusinessException.duplicate("Ya existe un proveedor registrado con ese NIT");
        }
        if (command.firstLocationName() == null || command.firstLocationName().isBlank()) {
            throw BusinessException.rule("Se requiere al menos una sede para registrar el proveedor");
        }

        UUID userId = userProvisioning.provisionUser(command.contactEmail(), command.password());

        Provider provider = Provider.register(UUID.randomUUID(), userId, command.legalName(), command.nit(),
                command.category(), command.contactEmail(), command.contactPhone(), command.timezone(),
                command.minBookingNoticeMinutes(), command.maxBookingNoticeMinutes(),
                command.freeCancellationWindowMinutes());
        provider = providerRepository.save(provider);

        providerStaffRepository.saveOwner(new ProviderOwner(UUID.randomUUID(), userId, provider.getId()));

        Location location = Location.create(UUID.randomUUID(), provider.getId(), command.firstLocationName(),
                command.firstLocationAddress(), command.firstLocationCity(), command.timezone());
        locationRepository.save(location);

        return provider;
    }
}
