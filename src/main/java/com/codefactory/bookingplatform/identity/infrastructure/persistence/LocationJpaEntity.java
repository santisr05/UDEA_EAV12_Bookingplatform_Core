package com.codefactory.bookingplatform.identity.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "locations")
public class LocationJpaEntity {
    @Id
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 80)
    private String city;

    @Column(nullable = false, length = 60)
    private String timezone;

    @Column(nullable = false, length = 20)
    private String status;

    protected LocationJpaEntity() {
    }

    public LocationJpaEntity(UUID id, UUID providerId, String name, String address, String city,
                              String timezone, String status) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.timezone = timezone;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getProviderId() { return providerId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getTimezone() { return timezone; }
    public String getStatus() { return status; }
}
