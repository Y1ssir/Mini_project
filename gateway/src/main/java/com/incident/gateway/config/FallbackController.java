package com.incident.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Contrôleur de fallback pour le Circuit Breaker.
 *
 * Quand un service backend est indisponible, le circuit breaker redirige
 * la requête vers ces endpoints au lieu de laisser l'utilisateur attendre.
 *
 * Retourne un JSON d'erreur structuré avec :
 * - Le service indisponible
 * - Un message d'erreur clair
 * - Un timestamp
 * - Un code HTTP 503 (Service Unavailable)
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        log.warn("Fallback activé pour USER-SERVICE");
        return buildFallbackResponse("user-service", "Le service utilisateurs est temporairement indisponible.");
    }

    @GetMapping("/incident-service")
    public Mono<ResponseEntity<Map<String, Object>>> incidentServiceFallback() {
        log.warn("Fallback activé pour INCIDENT-SERVICE");
        return buildFallbackResponse("incident-service", "Le service incidents est temporairement indisponible.");
    }

    @GetMapping("/comment-service")
    public Mono<ResponseEntity<Map<String, Object>>> commentServiceFallback() {
        log.warn("Fallback activé pour COMMENT-SERVICE");
        return buildFallbackResponse("comment-service", "Le service commentaires est temporairement indisponible.");
    }

    @GetMapping("/chat-service")
    public Mono<ResponseEntity<Map<String, Object>>> chatServiceFallback() {
        log.warn("Fallback activé pour CHAT-SERVICE");
        return buildFallbackResponse("chat-service", "Le service chat est temporairement indisponible.");
    }

    private Mono<ResponseEntity<Map<String, Object>>> buildFallbackResponse(
            String service, String message) {
        Map<String, Object> body = Map.of(
            "status", 503,
            "error", "Service Unavailable",
            "service", service,
            "message", message,
            "timestamp", Instant.now().toString(),
            "suggestion", "Veuillez réessayer dans quelques instants."
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}
