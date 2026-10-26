package com.codefactory.bookingplatform.identity.domain;

import com.codefactory.bookingplatform.identity.domain.model.Client;
import com.codefactory.bookingplatform.identity.domain.model.ClientStatus;
import com.codefactory.bookingplatform.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 15);

    @Test
    void register_succeeds_for_adult_and_starts_pending_verification() {
        Client client = Client.register(UUID.randomUUID(), "Ana Perez", "123456", TODAY.minusYears(20),
                "ana@example.com", "3000000000", "Medellin", "EMAIL", TODAY);

        assertThat(client.getStatus()).isEqualTo(ClientStatus.PENDING_VERIFICATION);
    }

    @Test
    void register_rejects_minor() {
        assertThatThrownBy(() -> Client.register(UUID.randomUUID(), "Juan", "7890", TODAY.minusYears(17),
                "juan@example.com", null, null, null, TODAY))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mayor de edad");
    }

    @Test
    void register_rejects_invalid_email() {
        assertThatThrownBy(() -> Client.register(UUID.randomUUID(), "Juan", "7890", TODAY.minusYears(20),
                "not-an-email", null, null, null, TODAY))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void ensureCanBook_throws_when_not_verified() {
        Client client = Client.register(UUID.randomUUID(), "Ana", "1", TODAY.minusYears(20), "a@a.com", null, null, null, TODAY);
        assertThatThrownBy(client::ensureCanBook).isInstanceOf(BusinessException.class);
    }

    @Test
    void markVerified_allows_booking_afterwards() {
        Client client = Client.register(UUID.randomUUID(), "Ana", "1", TODAY.minusYears(20), "a@a.com", null, null, null, TODAY);
        client.markVerified();
        client.ensureCanBook(); // no deberia lanzar
        assertThat(client.getStatus()).isEqualTo(ClientStatus.ACTIVE);
    }

    @Test
    void markVerified_twice_throws() {
        Client client = Client.register(UUID.randomUUID(), "Ana", "1", TODAY.minusYears(20), "a@a.com", null, null, null, TODAY);
        client.markVerified();
        assertThatThrownBy(client::markVerified).isInstanceOf(BusinessException.class);
    }
}
