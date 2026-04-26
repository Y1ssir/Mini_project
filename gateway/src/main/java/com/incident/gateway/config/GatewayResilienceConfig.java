package com.incident.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration Resilience4j pour le Gateway.
 *
 * Circuit Breaker : Protège les services backend contre la surcharge.
 * Si un service répond trop lentement ou renvoie des erreurs,
 * le circuit s'ouvre et les requêtes sont immédiatement rejetées
 * (avec une réponse de fallback) pendant une période de récupération.
 *
 * États du Circuit Breaker :
 * - CLOSED (normal) : Les requêtes passent normalement
 * - OPEN (dégradé) : Les requêtes sont rejetées immédiatement (fallback)
 * - HALF-OPEN (test) : Quelques requêtes passent pour tester la récupération
 */
@Configuration
public class GatewayResilienceConfig {

    /**
     * Configuration par défaut du Circuit Breaker.
     * Appliquée à tous les services sauf configuration spécifique.
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id ->
            new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                    // Seuil de taux d'échec pour ouvrir le circuit (50%)
                    .failureRateThreshold(50)
                    // Temps d'attente en état OPEN avant de passer à HALF-OPEN
                    .waitDurationInOpenState(Duration.ofSeconds(30))
                    // Nombre d'appels en HALF-OPEN pour tester la récupération
                    .permittedNumberOfCallsInHalfOpenState(10)
                    // Taille de la fenêtre glissante (nombre de requêtes)
                    .slidingWindowSize(20)
                    // Nombre minimum d'appels avant de calculer le taux d'échec
                    .minimumNumberOfCalls(5)
                    // Seuil de taux de calls lents pour ouvrir le circuit (80%)
                    .slowCallRateThreshold(80)
                    // Durée considérée comme un appel lent
                    .slowCallDurationThreshold(Duration.ofSeconds(5))
                    .build()
                )
                .timeLimiterConfig(TimeLimiterConfig.custom()
                    // Timeout global pour chaque requête
                    .timeoutDuration(Duration.ofSeconds(10))
                    .build()
                )
                .build()
        );
    }
}
