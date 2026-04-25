package com.incident.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Filtre global de logging et traçabilité.
 *
 * Ce filtre est appliqué à TOUTES les requêtes qui passent par le Gateway.
 * Il :
 * - Génère un ID de corrélation unique (X-Correlation-ID) pour chaque requête
 * - Log les requêtes entrantes (méthode, chemin, IP client)
 * - Log les réponses sortantes (status code, durée)
 *
 * L'ID de corrélation est propagé vers les microservices et permet de
 * tracer une requête à travers tous les services (distributed tracing simplifié).
 */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Génération ou récupération de l'ID de corrélation
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;
        final long startTime = System.currentTimeMillis();

        // Ajout du header de corrélation à la requête propagée
        ServerHttpRequest mutatedRequest = request.mutate()
            .header(CORRELATION_ID_HEADER, finalCorrelationId)
            .build();

        log.info("[{}] → {} {} (from: {})",
            finalCorrelationId,
            request.getMethod(),
            request.getURI().getPath(),
            request.getRemoteAddress()
        );

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
            .then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                long duration = System.currentTimeMillis() - startTime;
                log.info("[{}] ← {} {} {}ms",
                    finalCorrelationId,
                    response.getStatusCode(),
                    request.getURI().getPath(),
                    duration
                );
            }));
    }

    @Override
    public int getOrder() {
        // Priorité haute : ce filtre s'exécute en premier
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
