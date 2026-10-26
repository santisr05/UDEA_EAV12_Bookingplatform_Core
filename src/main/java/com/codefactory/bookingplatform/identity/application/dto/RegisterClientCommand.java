package com.codefactory.bookingplatform.identity.application.dto;

import java.time.LocalDate;

public record RegisterClientCommand(
        String fullName,
        String document,
        LocalDate birthDate,
        String email,
        String password,
        String phone,
        String city,
        String notificationChannel
) {}
