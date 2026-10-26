package com.codefactory.bookingplatform.auth.domain.service;

import java.util.regex.Pattern;

/**
 * HU-021, escenario "Contrasena que no cumple la politica": minimo 8
 * caracteres, al menos una mayuscula, una minuscula y un digito.
 */
public final class PasswordPolicy {

    private static final Pattern UPPER = Pattern.compile("[A-Z]");
    private static final Pattern LOWER = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final int MIN_LENGTH = 8;

    private PasswordPolicy() {
    }

    public static boolean isValid(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }
        return UPPER.matcher(password).find()
                && LOWER.matcher(password).find()
                && DIGIT.matcher(password).find();
    }
}
