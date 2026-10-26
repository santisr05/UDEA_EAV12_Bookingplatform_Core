package com.codefactory.bookingplatform.identity.api;

import com.codefactory.bookingplatform.identity.application.ListUsersUseCase;
import com.codefactory.bookingplatform.identity.application.SuspendProviderUseCase;
import com.codefactory.bookingplatform.identity.infrastructure.persistence.ClientJpaEntity;
import com.codefactory.bookingplatform.identity.infrastructure.persistence.ProviderJpaEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

/**
 * HU-003. Todas las rutas son exclusivas de administradores de la plataforma.
 */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminUserController {

    private final ListUsersUseCase listUsersUseCase;
    private final SuspendProviderUseCase suspendProviderUseCase;

    public AdminUserController(ListUsersUseCase listUsersUseCase, SuspendProviderUseCase suspendProviderUseCase) {
        this.listUsersUseCase = listUsersUseCase;
        this.suspendProviderUseCase = suspendProviderUseCase;
    }

    public record SuspendRequest(@NotBlank String reason, boolean force) {}
    public record SuspendResponse(long affectedBookings, boolean suspended) {}

    @GetMapping("/clients")
    public Page<ClientJpaEntity> listClients(@RequestParam(required = false) String status,
                                              @RequestParam(required = false) String city,
                                              @RequestParam(required = false) Instant from,
                                              @RequestParam(required = false) Instant to,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return listUsersUseCase.searchClients(status, city, from, to, PageRequest.of(page, size));
    }

    @GetMapping("/providers")
    public Page<ProviderJpaEntity> listProviders(@RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return listUsersUseCase.searchProviders(status, category, PageRequest.of(page, size));
    }

    @GetMapping(value = "/clients/export", produces = "text/csv")
    public ResponseEntity<String> exportClients(@RequestParam(required = false) String status) {
        Page<ClientJpaEntity> clients = listUsersUseCase.searchClients(status, null, null, null, PageRequest.of(0, 10_000));
        StringBuilder csv = new StringBuilder("id,full_name,document,email,city,status,created_at\n");
        clients.forEach(c -> csv.append(String.join(",",
                c.getId().toString(), safe(c.getFullName()), safe(c.getDocument()), safe(c.getEmail()),
                safe(c.getCity()), safe(c.getStatus()), c.getCreatedAt().toString())).append('\n'));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"clients.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    @PostMapping("/providers/{providerId}/suspension")
    public ResponseEntity<SuspendResponse> suspendProvider(@PathVariable UUID providerId,
                                                            @RequestBody SuspendRequest request) {
        SuspendProviderUseCase.Result result = suspendProviderUseCase.suspend(providerId, request.reason(), request.force());
        return ResponseEntity.ok(new SuspendResponse(result.affectedBookings(), result.suspended()));
    }

    private static String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
