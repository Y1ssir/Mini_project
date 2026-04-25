package com.incident.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Sécurité du Config Server.
 *
 * Les credentials sont définis dans application.yml via spring.security.user.
 * Les microservices clients doivent passer ces credentials dans leur
 * bootstrap.yml ou via les variables d'environnement CONFIG_SERVER_USERNAME/PASSWORD.
 */
@Configuration
@EnableWebSecurity
public class ConfigSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Healthcheck accessible sans auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // Toutes les autres routes (lecture de config) nécessitent auth
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {});

        return http.build();
    }
}
