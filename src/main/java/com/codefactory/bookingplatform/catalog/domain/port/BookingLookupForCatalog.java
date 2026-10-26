package com.codefactory.bookingplatform.catalog.domain.port;

import java.util.UUID;

/**
 * Puerto que implementa el modulo booking (inversion de dependencia): le
 * permite a catalog saber si un servicio tiene reservas sin depender de
 * booking en tiempo de compilacion (HU-004, "Desactivar un servicio con
 * reservas").
 */
public interface BookingLookupForCatalog {
    boolean hasAnyBooking(UUID serviceId);
}
