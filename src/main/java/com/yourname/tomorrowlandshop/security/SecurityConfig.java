package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.service.JwtService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter,
                                            LoginAuditFilter loginAuditFilter) throws Exception {
        // Cookie-based CSRF token is HttpOnly (default) so XSS cannot read it; Thymeleaf forms get _csrf via RequestDataValueProcessor.
        // /api/** uses Authorization: Bearer (JWT), not session cookies for auth, so CSRF does not apply the same way (OWASP: CSRF targets cookie-authenticated requests).
        http.csrf(csrf -> csrf
                .csrfTokenRepository(new CookieCsrfTokenRepository())
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/**"))); // NOSONAR (java:S4502) — stateless JWT for /api/**
        http.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(401);
        }));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/orders/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/orders/history").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        http.formLogin(Customizer.withDefaults());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(loginAuditFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    JwtAuthFilter jwtAuthFilter(ObjectProvider<JwtService> jwtServiceProvider) {
        return new JwtAuthFilter(jwtServiceProvider.getIfAvailable());
    }

    @Bean
    LoginAuditFilter loginAuditFilter(ObjectProvider<LoginAuditRepository> loginAuditRepositoryProvider) {
        return new LoginAuditFilter(loginAuditRepositoryProvider.getIfAvailable());
    }
}
