package com.codefactory.bookingplatform.auth.domain.port;

import com.codefactory.bookingplatform.auth.domain.model.LoginAttempt;

import java.util.List;

public interface LoginAttemptRepository {
    void record(LoginAttempt attempt);
    List<LoginAttempt> findRecentByEmail(String email, int limit);
}
