package com.codefactory.bookingplatform.identity.domain.service;

import java.time.LocalDate;
import java.time.Period;

/**
 * HU-001, escenario "Persona menor de edad": el autorregistro exige 18 anos
 * o mas, calculados a la fecha del registro.
 */
public final class AgePolicy {

    public static final int MINIMUM_AGE = 18;

    private AgePolicy() {
    }

    public static boolean isOldEnough(LocalDate birthDate, LocalDate referenceDate) {
        if (birthDate == null || referenceDate == null) {
            return false;
        }
        return Period.between(birthDate, referenceDate).getYears() >= MINIMUM_AGE;
    }
}
