package com.incident.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration de sécurité du Gateway.
 *
 * Utilise Spring Security WebFlux (réactif), compatible avec Spring Cloud Gateway.
 * ⚠️ NE PAS utiliser @EnableWebSecurity (servlet) - utiliser @EnableWebFluxSecurity.
 *
 * Flux de validation d'un token JWT :
 * 1. Le client envoie Authorization: Bearer <token>
 * 2. Le Gateway extrait le token
 * 3. Requête vers Keycloak JWKS endpoint pour récupérer les clés publiques
 * 4. Vérification de la signature du token
 * 5. Vérification de l'issuer, expiration, audience
 * 6. Si valide → la requête est routée vers le microservice
 * 7. Si invalide → réponse 401 Unauthorized
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    /**
     * URI de l'issuer Keycloak (realm incident-realm).
     * Spring Security utilisera automatiquement /.well-known/openid-configuration
     * pour découvrir le JWKS endpoint.
     */
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            // ================================================================
            // CSRF : Désactivé (API stateless avec JWT)
            // ================================================================
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            // ================================================================
            // CORS : Configuration permissive pour le développement
            // ================================================================
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ================================================================
            // Autorisation des routes
            // ================================================================
            .authorizeExchange(exchanges -> exchanges
                // Routes publiques (pas de token requis)
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/api/auth/**").permitAll()

                // Autoriser toutes les requêtes OPTIONS (preflight CORS)
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Toutes les autres routes nécessitent un token JWT valide
                .anyExchange().authenticated()
            )

            // ================================================================
            // OAuth2 Resource Server : Validation JWT via Keycloak
            // ================================================================
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            );

        return http.build();
    }

    /**
     * Décodeur JWT qui valide les tokens Keycloak.
     * Utilise le endpoint JWKS de Keycloak pour vérifier les signatures.
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromIssuerLocation(issuerUri);
    }

    /**
     * Configuration CORS pour le développement.
     * ⚠️ En production : restreindre les origines aux domaines autorisés.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origines autorisées - En dev : toutes les origines
        // En prod : remplacer par la liste des domaines frontend
        config.setAllowedOriginPatterns(List.of("*"));

        // Méthodes HTTP autorisées
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));

        // Headers autorisés dans les requêtes
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Correlation-ID"  // Header de traçabilité personnalisé
        ));

        // Headers exposés dans les réponses (accessible côté client)
        config.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "X-Correlation-ID"
        ));

        // Autoriser l'envoi de cookies/credentials
        config.setAllowCredentials(true);

        // Durée de cache du preflight (en secondes)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
