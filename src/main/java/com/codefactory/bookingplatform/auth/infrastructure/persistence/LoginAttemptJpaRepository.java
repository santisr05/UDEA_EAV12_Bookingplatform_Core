package com.codefactory.bookingplatform.auth.infrastructure.persistence;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoginAttemptJpaRepository extends JpaRepository<LoginAttemptJpaEntity, UUID> {
    List<LoginAttemptJpaEntity> findByEmailOrderByOccurredAtDesc(String email, Limit limit);
}
