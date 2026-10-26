package com.codefactory.bookingplatform.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion del proyecto de Supabase usado unicamente como proveedor de
 * autenticacion (GoTrue). La persistencia de negocio vive en MySQL/Aiven,
 * no en la base de datos de Supabase (ver ADR-05: migracion de motor de BD).
 */
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProperties {

    private String url;
    private String secretKey;
    private String jwksUrl;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public String getJwksUrl() { return jwksUrl; }
    public void setJwksUrl(String jwksUrl) { this.jwksUrl = jwksUrl; }
}
