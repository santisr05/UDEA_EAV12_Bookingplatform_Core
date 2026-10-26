package com.codefactory.bookingplatform.auth.infrastructure.rbac;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "permission")
public class PermissionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 60)
    private String code;

    @Column(length = 200)
    private String description;

    protected PermissionJpaEntity() {
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
}
