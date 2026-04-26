package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.service.JwtService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    private static final String PRODUCTS_PATH = "/products";
    private static final String API_PATH = "/api";
    private static final String ADMIN_PATH = "/admin";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
                                                        PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    private static void sendApiForbidden(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    private static void redirectToProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ctx = request.getContextPath();
        response.sendRedirect(ctx + PRODUCTS_PATH);
    }

    private static boolean isBearerApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String ctx = request.getContextPath();
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        return path.startsWith(ctx + API_PATH + "/") && authorization != null && authorization.startsWith(BEARER_PREFIX);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter,
                                            LoginAuditFilter loginAuditFilter,
                                            DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        http.authenticationProvider(daoAuthenticationProvider);
        http.csrf(csrf -> csrf
                .csrfTokenRepository(new CookieCsrfTokenRepository())
                .ignoringRequestMatchers(SecurityConfig::isBearerApiRequest));
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String path = request.getRequestURI();
                    String ctx = request.getContextPath();
                    if (path.startsWith(ctx + API_PATH)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    if (path.startsWith(ctx + ADMIN_PATH)) {
                        redirectToProducts(request, response);
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    String path = request.getRequestURI();
                    String ctx = request.getContextPath();
                    if (path.startsWith(ctx + API_PATH)) {
                        sendApiForbidden(response);
                        return;
                    }
                    if (path.startsWith(ctx + ADMIN_PATH)) {
                        redirectToProducts(request, response);
                        return;
                    }
                    sendApiForbidden(response);
                }));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").permitAll()
                .requestMatchers("/cart/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/orders/checkout").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/orders/paypal/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/orders/confirmation").permitAll()
                .requestMatchers(HttpMethod.POST, "/orders/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/orders/history").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl(PRODUCTS_PATH, true));
        http.logout(logout -> logout.logoutSuccessUrl(PRODUCTS_PATH).permitAll());
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
