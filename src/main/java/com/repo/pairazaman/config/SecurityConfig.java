package com.repo.pairazaman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración temporal de seguridad para el módulo de clientes.
 *
 * <p>El proyecto incluye spring-boot-starter-security. Sin esta configuración,
 * todos los endpoints quedarían protegidos con autenticación básica y el
 * frontend recibiría 401. Cuando se implemente el módulo de usuarios/roles,
 * se debe restringir /api/clientes a los roles correspondientes.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST sin sesión ni formularios: CSRF no aplica.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll());

        return http.build();
    }
}
