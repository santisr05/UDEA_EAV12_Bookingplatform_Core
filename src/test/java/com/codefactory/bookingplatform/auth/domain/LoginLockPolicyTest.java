package com.codefactory.bookingplatform.auth.domain;

import com.codefactory.bookingplatform.auth.domain.model.LoginAttempt;
import com.codefactory.bookingplatform.auth.domain.service.LoginLockPolicy;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoginLockPolicyTest {

    @Test
    void not_locked_with_fewer_than_5_failures() {
        Instant now = Instant.now();
        List<LoginAttempt> attempts = failuresEndingAt(now, 4);
        assertThat(LoginLockPolicy.isLocked(attempts, now)).isFalse();
    }

    @Test
    void locked_with_5_consecutive_failures_within_window() {
        Instant now = Instant.now();
        List<LoginAttempt> attempts = failuresEndingAt(now, 5);
        assertThat(LoginLockPolicy.isLocked(attempts, now)).isTrue();
    }

    @Test
    void not_locked_when_a_success_breaks_the_streak() {
        Instant now = Instant.now();
        List<LoginAttempt> attempts = new ArrayList<>();
        attempts.add(new LoginAttempt("a@a.com", false, now.minusSeconds(60)));
        attempts.add(new LoginAttempt("a@a.com", false, now.minusSeconds(120)));
        attempts.add(new LoginAttempt("a@a.com", true, now.minusSeconds(180)));
        attempts.add(new LoginAttempt("a@a.com", false, now.minusSeconds(240)));
        attempts.add(new LoginAttempt("a@a.com", false, now.minusSeconds(300)));
        assertThat(LoginLockPolicy.isLocked(attempts, now)).isFalse();
    }

    @Test
    void not_locked_when_failures_are_outside_the_15_minute_window() {
        Instant now = Instant.now();
        List<LoginAttempt> attempts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            attempts.add(new LoginAttempt("a@a.com", false, now.minusSeconds(1000 + i * 60L)));
        }
        assertThat(LoginLockPolicy.isLocked(attempts, now)).isFalse();
    }

    private List<LoginAttempt> failuresEndingAt(Instant now, int count) {
        List<LoginAttempt> attempts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            attempts.add(new LoginAttempt("a@a.com", false, now.minusSeconds(i * 30L)));
        }
        return attempts;
    }
}
