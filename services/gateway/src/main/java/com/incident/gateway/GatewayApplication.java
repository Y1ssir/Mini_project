package com.incident.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway - Point d'Entrée Unique
 *
 * Ce service est le seul point d'accès externe à la plateforme.
 * Il est responsable de :
 *
 * 1. ROUTAGE : Redirige les requêtes vers les bons microservices
 *    via la découverte de services Eureka (load balancing inclus).
 *    Exemple : GET /api/users/** → lb://USER-SERVICE/api/users/**
 *
 * 2. SÉCURITÉ : Valide les tokens JWT émis par Keycloak.
 *    Les requêtes sans token valide reçoivent une réponse 401.
 *    Les tokens sont validés via le endpoint JWKS de Keycloak.
 *
 * 3. CORS : Politique de Cross-Origin Resource Sharing configurée
 *    pour les clients front-end (React, Angular, etc.).
 *
 * 4. RÉSILIENCE : Circuit breaker et rate limiting via Resilience4j.
 *    Protège les services backend contre la surcharge.
 *
 * Architecture : Spring Cloud Gateway est basé sur WebFlux (réactif),
 * incompatible avec le servlet stack classique.
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
