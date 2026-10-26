package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.UUID;

public record CatalogSearchRow(UUID serviceId, String serviceName, String providerName, int durationMinutes,
                                BigDecimal price, String city) {
}
