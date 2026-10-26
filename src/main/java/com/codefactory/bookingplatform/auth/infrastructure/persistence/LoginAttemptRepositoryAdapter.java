package com.codefactory.bookingplatform.auth.infrastructure.persistence;

import com.codefactory.bookingplatform.auth.domain.model.LoginAttempt;
import com.codefactory.bookingplatform.auth.domain.port.LoginAttemptRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoginAttemptRepositoryAdapter implements LoginAttemptRepository {

    private final LoginAttemptJpaRepository jpaRepository;

    public LoginAttemptRepositoryAdapter(LoginAttemptJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void record(LoginAttempt attempt) {
        jpaRepository.save(new LoginAttemptJpaEntity(attempt.email(), attempt.successful(), attempt.occurredAt()));
    }

    @Override
    public List<LoginAttempt> findRecentByEmail(String email, int limit) {
        return jpaRepository.findByEmailOrderByOccurredAtDesc(email, Limit.of(limit)).stream()
                .map(e -> new LoginAttempt(e.getEmail(), e.isSuccessful(), e.getOccurredAt()))
                .toList();
    }
}
