package com.codefactory.bookingplatform.catalog.domain;

import com.codefactory.bookingplatform.catalog.domain.model.CapacityType;
import com.codefactory.bookingplatform.catalog.domain.model.ServiceOffering;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceOfferingTest {

    @Test
    void create_rejects_zero_duration() {
        assertThatThrownBy(() -> ServiceOffering.create(UUID.randomUUID(), UUID.randomUUID(), "Corte", "desc", 0, 0,
                0, BigDecimal.TEN, UUID.randomUUID(), CapacityType.INDIVIDUAL, 1))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void create_rejects_negative_price() {
        assertThatThrownBy(() -> ServiceOffering.create(UUID.randomUUID(), UUID.randomUUID(), "Corte", "desc", 30, 0,
                0, BigDecimal.valueOf(-1), UUID.randomUUID(), CapacityType.INDIVIDUAL, 1))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void group_service_with_capacity_1_is_rejected() {
        assertThatThrownBy(() -> ServiceOffering.create(UUID.randomUUID(), UUID.randomUUID(), "Yoga", "desc", 60, 0,
                0, BigDecimal.TEN, UUID.randomUUID(), CapacityType.GROUP, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("grupal");
    }

    @Test
    void group_service_with_valid_capacity_is_accepted() {
        ServiceOffering service = ServiceOffering.create(UUID.randomUUID(), UUID.randomUUID(), "Yoga", "desc", 60, 0,
                0, BigDecimal.TEN, UUID.randomUUID(), CapacityType.GROUP, 10);
        assertThat(service.getMaxCapacity()).isEqualTo(10);
    }

    @Test
    void individual_service_forces_capacity_to_1() {
        ServiceOffering service = ServiceOffering.create(UUID.randomUUID(), UUID.randomUUID(), "Corte", "desc", 30, 0,
                0, BigDecimal.TEN, UUID.randomUUID(), CapacityType.INDIVIDUAL, 5);
        assertThat(service.getMaxCapacity()).isEqualTo(1);
    }

    @Test
    void deactivate_never_deletes_just_changes_status() {
        ServiceOffering service = ServiceOffering.create(UUID.randomUUID(), UUID.randomUUID(), "Corte", "desc", 30, 0,
                0, BigDecimal.TEN, UUID.randomUUID(), CapacityType.INDIVIDUAL, 1);
        service.deactivate();
        assertThat(service.isActive()).isFalse();
    }
}
