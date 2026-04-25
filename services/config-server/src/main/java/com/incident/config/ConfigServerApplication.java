package com.incident.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server - Configuration Centralisée
 *
 * Ce service lit les fichiers de configuration depuis un dépôt Git
 * (local ou distant) et les expose via une API REST.
 *
 * Chaque microservice au démarrage interroge ce serveur pour récupérer
 * sa configuration via l'URL : http://config-server:8888/{service-name}/{profile}
 *
 * Convention de nommage des fichiers de config :
 * - application.yml          → config commune à TOUS les services
 * - {service-name}.yml       → config spécifique à un service
 * - {service-name}-{profile}.yml → config spécifique + profil (dev, prod...)
 *
 * Sécurité : accès protégé par Basic Auth
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
