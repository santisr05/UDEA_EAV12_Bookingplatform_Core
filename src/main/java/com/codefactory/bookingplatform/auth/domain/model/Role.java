package com.codefactory.bookingplatform.auth.domain.model;

import java.util.UUID;

public record Role(UUID id, String name) {

    public static final String ADMIN = "ADMINISTRADOR";
    public static final String MANAGER = "GERENTE";
    public static final String PROVIDER = "PROVEEDOR";
    public static final String RECEPTIONIST = "RECEPCIONISTA";
    public static final String CLIENT = "CLIENTE";
}
