package com.incident.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server - Service Register
 *
 * Ce service joue le rôle de registre central de découverte pour tous les
 * microservices de la plateforme. Chaque microservice s'y enregistre au
 * démarrage et peut interroger le registre pour localiser les autres services.
 *
 * Caractéristiques :
 * - Serveur Eureka "standalone" (ne s'enregistre pas lui-même)
 * - Dashboard accessible sur http://localhost:8761
 * - Healthcheck disponible sur /actuator/health
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
