package com.codefactory.bookingplatform.catalog.domain.port;

import java.util.UUID;

/**
 * Puerto que catalog define pero que implementa scheduling (inversion de
 * dependencia), ya que las instancias concretas de recurso viven alli.
 */
public interface ResourceInstanceAvailabilityQuery {
    boolean hasAvailableInstance(UUID resourceTypeId);
}
