package com.repo.pairazaman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/error").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/usuarios/**", "/reportes/**", "/planes/**", "/alertas/**").hasRole("GERENTE")
                        .requestMatchers("/camaras/**").hasRole("GERENTE")
                        .requestMatchers("/contratos/**").hasAnyRole("GERENTE", "SECRETARIA")
                        .requestMatchers("/clientes/**").hasAnyRole("GERENTE", "SECRETARIA", "COBRANZA")
                        .requestMatchers("/instalaciones/**", "/reclamos/**").hasAnyRole("GERENTE", "SECRETARIA", "TECNICO")
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