package com.codefactory.bookingplatform.auth.infrastructure.supabase;

import com.codefactory.bookingplatform.auth.domain.port.IdentityProviderPort;
import com.codefactory.bookingplatform.shared.config.SupabaseProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

/**
 * Adaptador de infraestructura que traduce el puerto IdentityProviderPort a
 * llamadas HTTP reales contra la API de Supabase Auth (GoTrue). Es el unico
 * lugar del sistema que conoce la forma concreta de esa API (ADR-03).
 */
@Component
public class GoTrueClient implements IdentityProviderPort {

    private final WebClient webClient;

    public GoTrueClient(SupabaseProperties properties) {
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl() + "/auth/v1")
                .defaultHeader("apikey", properties.getSecretKey())
                .defaultHeader("Authorization", "Bearer " + properties.getSecretKey())
                .build();
    }

    @Override
    public UUID createUser(String email, String password) {
        Map<?, ?> response = webClient.post()
                .uri("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", email, "password", password, "email_confirm", false))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return UUID.fromString(String.valueOf(response.get("id")));
    }

    @Override
    public void triggerEmailVerification(String email) {
        webClient.post()
                .uri("/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", email, "type", "signup"))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public void verifyEmailOtp(String email, String token) {
        webClient.post()
                .uri("/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", email, "token", token, "type", "signup"))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public AuthResult login(String email, String password) {
        Map<?, ?> response = webClient.post()
                .uri("/token?grant_type=password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", email, "password", password))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        Map<?, ?> user = (Map<?, ?>) response.get("user");
        return new AuthResult(
                String.valueOf(response.get("access_token")),
                String.valueOf(response.get("refresh_token")),
                UUID.fromString(String.valueOf(user.get("id"))));
    }

    @Override
    public void logout(String accessToken) {
        webClient.post()
                .uri("/logout")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public void requestPasswordRecovery(String email) {
        webClient.post()
                .uri("/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", email))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public void resetPassword(String recoveryToken, String newPassword) {
        webClient.put()
                .uri("/user")
                .header("Authorization", "Bearer " + recoveryToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("password", newPassword))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
