package com.incident.eureka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité pour le dashboard Eureka.
 *
 * En production, le dashboard est protégé par Basic Auth.
 * Les autres microservices s'authentifient aussi via ces credentials
 * dans leur URL d'enregistrement Eureka.
 */
@Configuration
@EnableWebSecurity
public class EurekaSecurityConfig {

    @Value("${spring.security.user.name:admin}")
    private String username;

    @Value("${spring.security.user.password:admin}")
    private String password;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactive CSRF car Eureka utilise des requêtes REST internes
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/eureka/**")
            )
            .authorizeHttpRequests(auth -> auth
                // Autoriser les endpoints actuator sans auth
                .requestMatchers("/actuator/**").permitAll()
                // Tout le reste nécessite une authentification
                .anyRequest().authenticated()
            )
            // Dashboard accessible via Basic Auth
            .httpBasic(httpBasic -> {});

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username(username)
            .password(password)
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
