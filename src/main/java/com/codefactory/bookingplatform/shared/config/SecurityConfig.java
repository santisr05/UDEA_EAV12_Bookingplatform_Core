package com.codefactory.bookingplatform.shared.config;

import com.codefactory.bookingplatform.auth.infrastructure.rbac.DatabaseAuthoritiesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * La API es cerrada por defecto: cualquier ruta que no este en la lista de
 * rutas publicas exige un JWT valido, emitido por Supabase Auth y validado
 * contra su JWKS (ADR-03: autenticacion como proxy detras de un puerto propio).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ROUTES = {
            "/actuator/health/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/registrations",
            "/api/v1/registrations/verification-resends",
            "/api/v1/registrations/email-verifications",
            "/api/v1/auth/login",
            "/api/v1/auth/password-recovery-requests",
            "/api/v1/auth/password-resets",
            "/api/v1/catalog/**"
    };

    // HU-002: el autorregistro de proveedor es publico, igual que el de cliente.
    // Va aparte (con metodo HTTP explicito) porque las demas rutas bajo
    // /api/v1/providers/** (aprobacion, politica de operacion) SI exigen
    // autenticacion y rol.
    private static final String PROVIDER_SELF_REGISTRATION = "/api/v1/providers";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder,
                                           DatabaseAuthoritiesConverter databaseAuthoritiesConverter,
                                           CorsConfigurationSource corsConfigurationSource) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(databaseAuthoritiesConverter);

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.POST, PROVIDER_SELF_REGISTRATION).permitAll()
                        .requestMatchers(PUBLIC_ROUTES).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(SupabaseProperties supabaseProperties) {
        // Supabase firma sus JWT con ES256 (curva eliptica). El builder de
        // NimbusJwtDecoder acepta solo RS256 por defecto si no se le dice lo
        // contrario, por eso un token valido se rechazaba con "Another
        // algorithm expected".
        return NimbusJwtDecoder.withJwkSetUri(supabaseProperties.getJwksUrl())
                .jwsAlgorithm(SignatureAlgorithm.ES256)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.allowed-origins:*}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}