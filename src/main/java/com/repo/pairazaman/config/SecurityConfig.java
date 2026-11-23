package com.repo.pairazaman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // La API REST de clientes se consume desde React sin token CSRF.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/error").permitAll()
                        .requestMatchers("/login").permitAll()
                        // Módulo de clientes (React) — se protegerá por roles cuando el frontend
                        // integre el login. Por ahora queda accesible para no romper la API.
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/usuarios/**", "/reportes/**", "/planes/**", "/alertas/**").hasRole("GERENTE")
                        .requestMatchers("/camaras/**").hasRole("GERENTE")
                        .requestMatchers("/contratos/**").hasAnyRole("GERENTE", "SECRETARIA")
                        .requestMatchers("/clientes/**").hasAnyRole("GERENTE", "SECRETARIA", "COBRANZA")
                        // Programación de órdenes (RF-03): la gestiona Secretaria/Gerente. El técnico solo
                        // puede ver el listado de sus órdenes (GET /instalaciones) y el panel móvil.
                        .requestMatchers(HttpMethod.GET, "/instalaciones").hasAnyRole("GERENTE", "SECRETARIA", "TECNICO")
                        .requestMatchers("/instalaciones/mis-ordenes/**").hasAnyRole("GERENTE", "TECNICO")
                        .requestMatchers("/instalaciones/**", "/tecnicos/**").hasAnyRole("GERENTE", "SECRETARIA")
                        .requestMatchers("/reclamos/**").hasAnyRole("GERENTE", "SECRETARIA", "TECNICO")
                        .requestMatchers("/incidencias/eliminar/**").hasRole("GERENTE")
                        .requestMatchers("/incidencias/**").hasAnyRole("GERENTE", "TECNICO")
                        .requestMatchers("/pagos/**").hasAnyRole("GERENTE", "COBRANZA")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/acceso-denegado")
                );

        return http.build();
    }
}
