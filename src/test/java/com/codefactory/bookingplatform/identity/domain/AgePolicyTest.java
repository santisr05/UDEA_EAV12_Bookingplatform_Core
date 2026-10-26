package com.codefactory.bookingplatform.identity.domain;

import com.codefactory.bookingplatform.identity.domain.service.AgePolicy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AgePolicyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 15);

    @Test
    void isOldEnough_true_for_exactly_18() {
        assertThat(AgePolicy.isOldEnough(TODAY.minusYears(18), TODAY)).isTrue();
    }

    @Test
    void isOldEnough_false_one_day_before_turning_18() {
        assertThat(AgePolicy.isOldEnough(TODAY.minusYears(18).plusDays(1), TODAY)).isFalse();
    }

    @Test
    void isOldEnough_false_for_null_birthdate() {
        assertThat(AgePolicy.isOldEnough(null, TODAY)).isFalse();
    }
}
