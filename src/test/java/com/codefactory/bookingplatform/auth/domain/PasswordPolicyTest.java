package com.codefactory.bookingplatform.auth.domain;

import com.codefactory.bookingplatform.auth.domain.service.PasswordPolicy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordPolicyTest {

    @Test
    void rejects_short_password() {
        assertThat(PasswordPolicy.isValid("Abc1")).isFalse();
    }

    @Test
    void rejects_password_without_uppercase() {
        assertThat(PasswordPolicy.isValid("abcdefg1")).isFalse();
    }

    @Test
    void rejects_password_without_digit() {
        assertThat(PasswordPolicy.isValid("Abcdefgh")).isFalse();
    }

    @Test
    void accepts_valid_password() {
        assertThat(PasswordPolicy.isValid("Abcdefg1")).isTrue();
    }
}
