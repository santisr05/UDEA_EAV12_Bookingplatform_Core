package com.codefactory.bookingplatform.catalog.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "currency")
public class CurrencyJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, unique = true, length = 3)
    private String code;

    protected CurrencyJpaEntity() {
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
}
