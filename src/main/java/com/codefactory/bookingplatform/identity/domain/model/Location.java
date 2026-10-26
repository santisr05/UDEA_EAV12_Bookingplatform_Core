package com.codefactory.bookingplatform.identity.domain.model;

import com.codefactory.bookingplatform.shared.error.BusinessException;

import java.util.UUID;

public class Location {

    public enum Status { ACTIVE, INACTIVE }

    private final UUID id;
    private final UUID providerId;
    private String name;
    private String address;
    private String city;
    private String timezone;
    private Status status;

    private Location(UUID id, UUID providerId, String name, String address, String city, String timezone, Status status) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.timezone = timezone;
        this.status = status;
    }

    public static Location create(UUID id, UUID providerId, String name, String address, String city, String timezone) {
        if (name == null || name.isBlank()) {
            throw BusinessException.rule("El nombre de la sede es obligatorio");
        }
        return new Location(id, providerId, name, address, city, timezone, Status.ACTIVE);
    }

    public static Location rehydrate(UUID id, UUID providerId, String name, String address, String city,
                                      String timezone, Status status) {
        return new Location(id, providerId, name, address, city, timezone, status);
    }

    public UUID getId() { return id; }
    public UUID getProviderId() { return providerId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getTimezone() { return timezone; }
    public Status getStatus() { return status; }
}
