package com.codefactory.bookingplatform.auth.infrastructure.rbac;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "role")
public class RoleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 40)
    private String name;

    protected RoleJpaEntity() {
    }

    public RoleJpaEntity(String name) {
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
