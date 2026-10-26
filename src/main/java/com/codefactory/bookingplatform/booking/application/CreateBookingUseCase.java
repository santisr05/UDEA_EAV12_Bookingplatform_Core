package com.codefactory.bookingplatform.booking.application;

import com.codefactory.bookingplatform.booking.domain.model.Booking;
import com.codefactory.bookingplatform.booking.domain.model.BookingConditionSnapshot;
import com.codefactory.bookingplatform.booking.domain.port.BookingConditionSnapshotRepository;
import com.codefactory.bookingplatform.booking.domain.port.BookingRepository;
import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceOfferingRepository;
import com.codefactory.bookingplatform.catalog.domain.port.ServiceResourceRequirementRepository;
import com.codefactory.bookingplatform.identity.domain.model.Client;
import com.codefactory.bookingplatform.identity.domain.model.Provider;
import com.codefactory.bookingplatform.identity.domain.port.ClientRepository;
import com.codefactory.bookingplatform.identity.domain.port.ProviderRepository;
import com.codefactory.bookingplatform.scheduling.application.ReserveResourcesUseCase;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * HU-012. Sigue el flujo descrito en el diagrama de paquetes original:
 * booking "usa" identity (cliente que reserva), catalog (servicio pactado) y
 * scheduling (valida disponibilidad / asigna recursos) de forma directa,
 * porque esa es la direccion natural de la dependencia; solo las consultas
 * en sentido contrario (de esos modulos hacia booking) necesitan los puertos
 * invertidos que ya se definieron en cada uno.
 */
@Service
public class CreateBookingUseCase {

    private static final int NO_SHOW_LOOKBACK_DAYS = 90;
    private static final int NO_SHOW_THRESHOLD = 3;
    private static final int DEFAULT_MAX_RESCHEDULES = 3;

    private final BookingRepository bookingRepository;
    private final BookingConditionSnapshotRepository snapshotRepository;
    private final ClientRepository clientRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceResourceRequirementRepository requirementRepository;
    private final ProviderRepository providerRepository;
    private final ReserveResourcesUseCase reserveResourcesUseCase;

    public CreateBookingUseCase(BookingRepository bookingRepository,
                                 BookingConditionSnapshotRepository snapshotRepository,
                                 ClientRepository clientRepository,
                                 ServiceOfferingRepository serviceOfferingRepository,
                                 ServiceResourceRequirementRepository requirementRepository,
                                 ProviderRepository providerRepository,
                                 ReserveResourcesUseCase reserveResourcesUseCase) {
        this.bookingRepository = bookingRepository;
        this.snapshotRepository = snapshotRepository;
        this.clientRepository = clientRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.requirementRepository = requirementRepository;
        this.providerRepository = providerRepository;
        this.reserveResourcesUseCase = reserveResourcesUseCase;
    }

    @Transactional
    public Booking create(UUID clientId, UUID serviceId, UUID locationId, Instant startsAt, int attendees,
                           int scheduleGranularityMinutes) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> BusinessException.notFound("Cliente no encontrado"));
        client.ensureCanBook();

        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.notFound("Servicio no encontrado"));
        if (!service.isActive()) {
            throw BusinessException.rule("El servicio no esta activo");
        }

        Provider provider = providerRepository.findById(service.getProviderId())
                .orElseThrow(() -> BusinessException.notFound("Proveedor no encontrado"));

        int totalDuration = service.getDurationMinutes() + service.getPreparationMinutes() + service.getCleanupMinutes();
        Instant endsAt = startsAt.plusSeconds(totalDuration * 60L);

        if (bookingRepository.existsOverlapForClient(clientId, startsAt, endsAt)) {
            throw BusinessException.rule(
                    "Ya tiene una reserva activa en ese horario, aunque sea con otro proveedor");
        }

        Instant earliestAllowed = Instant.now().plus(Duration.ofMinutes(provider.getMinBookingNoticeMinutes()));
        Instant latestAllowed = Instant.now().plus(Duration.ofMinutes(provider.getMaxBookingNoticeMinutes()));
        if (startsAt.isBefore(earliestAllowed) || startsAt.isAfter(latestAllowed)) {
            throw BusinessException.rule("El horario elegido no respeta la anticipacion minima o maxima del proveedor");
        }

        long recentNoShows = bookingRepository.countNoShowsSince(clientId,
                Instant.now().minus(Duration.ofDays(NO_SHOW_LOOKBACK_DAYS)));
        boolean manualConfirmationRequired = recentNoShows > NO_SHOW_THRESHOLD;

        Booking booking = Booking.create(UUID.randomUUID(), clientId, serviceId, locationId, startsAt, endsAt,
                attendees, service.getPrice(), manualConfirmationRequired);

        List<ReserveResourcesUseCase.ResourceRequirement> required = requirementRepository.findByServiceId(serviceId).stream()
                .map(r -> new ReserveResourcesUseCase.ResourceRequirement(r.resourceTypeId(), r.quantityRequired()))
                .toList();
        reserveResourcesUseCase.reserve(booking.getId(), required, startsAt, endsAt, scheduleGranularityMinutes);

        booking = bookingRepository.save(booking);

        snapshotRepository.save(new BookingConditionSnapshot(booking.getId(), provider.getMinBookingNoticeMinutes(),
                provider.getMaxBookingNoticeMinutes(), provider.getFreeCancellationWindowMinutes(), totalDuration,
                DEFAULT_MAX_RESCHEDULES));

        return booking;
    }
}
