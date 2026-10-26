package com.codefactory.bookingplatform.booking.domain.model;

import java.util.UUID;

/**
 * HU-013/HU-014. Congela, al momento de crear la reserva, las condiciones
 * del proveedor y del servicio que importan para cancelar/reprogramar, para
 * que un cambio posterior en Provider o ServiceOffering no altere reservas
 * ya creadas.
 */
public record BookingConditionSnapshot(UUID bookingId, int minLeadTimeMinutes, int maxLeadTimeMinutes,
                                        int freeCancellationWindowMinutes, int durationMinutes,
                                        int maxReschedules) {
}
