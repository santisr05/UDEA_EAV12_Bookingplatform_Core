package com.codefactory.bookingplatform.identity.application.dto;

public record RegisterProviderCommand(
        String legalName,
        String nit,
        String category,
        String contactEmail,
        String contactPhone,
        String timezone,
        String password,
        int minBookingNoticeMinutes,
        int maxBookingNoticeMinutes,
        int freeCancellationWindowMinutes,
        String firstLocationName,
        String firstLocationAddress,
        String firstLocationCity
) {}
