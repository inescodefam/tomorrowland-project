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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    private static void sendApiForbidden(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    private static void redirectToProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ctx = request.getContextPath();
        response.sendRedirect(ctx + "/products");
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter,
                                            LoginAuditFilter loginAuditFilter) throws Exception {
        http.csrf(csrf -> csrf
                .csrfTokenRepository(new CookieCsrfTokenRepository())
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/**"))); 
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String path = request.getRequestURI();
                    String ctx = request.getContextPath();
                    if (path.startsWith(ctx + "/api")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    if (path.startsWith(ctx + "/admin")) {
                        redirectToProducts(request, response);
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    String path = request.getRequestURI();
                    String ctx = request.getContextPath();
                    if (path.startsWith(ctx + "/api")) {
                        sendApiForbidden(response);
                        return;
                    }
                    if (path.startsWith(ctx + "/admin")) {
                        redirectToProducts(request, response);
                        return;
                    }
                    sendApiForbidden(response);
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
