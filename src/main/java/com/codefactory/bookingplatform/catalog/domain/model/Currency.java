package com.codefactory.bookingplatform.catalog.domain.model;

import java.util.UUID;

public record Currency(UUID id, String name, String code) {
}
