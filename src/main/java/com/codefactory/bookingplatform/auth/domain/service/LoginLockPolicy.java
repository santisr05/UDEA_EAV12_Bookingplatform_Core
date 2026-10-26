package com.codefactory.bookingplatform.auth.domain.service;

import com.codefactory.bookingplatform.auth.domain.model.LoginAttempt;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * HU-021, escenario "Bloqueo tras varios intentos fallidos": 5 intentos
 * fallidos consecutivos dentro de una ventana de 15 minutos bloquean la
 * cuenta temporalmente. Un intento exitoso dentro de la ventana reinicia el
 * conteo porque solo se consideran los fallos *consecutivos* mas recientes.
 */
public final class LoginLockPolicy {

    public static final int MAX_ATTEMPTS = 5;
    public static final Duration WINDOW = Duration.ofMinutes(15);

    private LoginLockPolicy() {
    }

    public static boolean isLocked(List<LoginAttempt> recentAttemptsNewestFirst, Instant now) {
        int consecutiveFailures = 0;
        for (LoginAttempt attempt : recentAttemptsNewestFirst) {
            if (Duration.between(attempt.occurredAt(), now).compareTo(WINDOW) > 0) {
                break;
            }
            if (attempt.successful()) {
                break;
            }
            consecutiveFailures++;
            if (consecutiveFailures >= MAX_ATTEMPTS) {
                return true;
            }
        }
        return false;
    }
}
