package com.codefactory.bookingplatform.booking.api;

import com.codefactory.bookingplatform.booking.application.CancelBookingUseCase;
import com.codefactory.bookingplatform.booking.application.CreateBookingUseCase;
import com.codefactory.bookingplatform.booking.application.RescheduleBookingUseCase;
import com.codefactory.bookingplatform.booking.domain.model.Booking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final RescheduleBookingUseCase rescheduleBookingUseCase;

    public BookingController(CreateBookingUseCase createBookingUseCase, CancelBookingUseCase cancelBookingUseCase,
                              RescheduleBookingUseCase rescheduleBookingUseCase) {
        this.createBookingUseCase = createBookingUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
        this.rescheduleBookingUseCase = rescheduleBookingUseCase;
    }

    public record CreateRequest(@NotNull UUID serviceId, @NotNull UUID locationId, @NotNull Instant startsAt,
                                 @Min(1) int attendees, @NotNull Integer scheduleGranularityMinutes) {}
    public record BookingResponse(UUID bookingId, String code, String status) {}
    public record CancelRequest(String reason) {}
    public record CancelResponse(boolean late) {}
    public record RescheduleRequest(@NotNull Instant newStartsAt, @NotNull Integer scheduleGranularityMinutes) {}
    public record RescheduleResponse(boolean late) {}

    @PostMapping
    public ResponseEntity<BookingResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                   @Valid @RequestBody CreateRequest request) {
        UUID clientId = UUID.fromString(jwt.getSubject());
        Booking booking = createBookingUseCase.create(clientId, request.serviceId(), request.locationId(),
                request.startsAt(), request.attendees(), request.scheduleGranularityMinutes());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BookingResponse(booking.getId(), booking.getCode(), booking.getStatus().name()));
    }

    @PostMapping("/{bookingId}/client-cancellation")
    public ResponseEntity<CancelResponse> cancelByClient(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID bookingId) {
        UUID clientId = UUID.fromString(jwt.getSubject());
        var result = cancelBookingUseCase.cancelByClient(bookingId, clientId);
        return ResponseEntity.ok(new CancelResponse(result.late()));
    }

    @PostMapping("/{bookingId}/provider-cancellation")
    public ResponseEntity<Void> cancelByProvider(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID bookingId,
                                                  @RequestBody CancelRequest request) {
        UUID actorId = UUID.fromString(jwt.getSubject());
        cancelBookingUseCase.cancelByProvider(bookingId, actorId, request.reason());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{bookingId}/schedule")
    public ResponseEntity<RescheduleResponse> reschedule(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID bookingId,
                                                          @Valid @RequestBody RescheduleRequest request) {
        UUID clientId = UUID.fromString(jwt.getSubject());
        var result = rescheduleBookingUseCase.reschedule(bookingId, clientId, request.newStartsAt(),
                request.scheduleGranularityMinutes());
        return ResponseEntity.ok(new RescheduleResponse(result.late()));
    }
}
