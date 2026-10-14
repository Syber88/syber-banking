package com.syber.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/register/**", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginProcessingUrl("/api/v1/auth/login"))
                .logout(logout -> logout.logoutUrl("/api/v1/auth/logout"))
                // CSRF is ON. Because authentication is a cookie, the browser
                // attaches it to ANY request to this origin - including one a
                // malicious site triggers. The token defeats that: an attacker
                // can cause a request but cannot read the token to include.
                // withHttpOnlyFalse lets a JS client read the XSRF-TOKEN cookie
                // and echo it in the X-XSRF-TOKEN header.
                .csrf(csrf -> csrf.csrfTokenRepository(
                        CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // Rotate the session on login (default, stated explicitly so a
                // future edit does not silently remove session-fixation cover)
                // and cap concurrent sessions per user.
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.changeSessionId())
                        .maximumSessions(2))
                .build();
    }
}
