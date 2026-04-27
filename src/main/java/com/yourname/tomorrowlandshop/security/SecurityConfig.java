package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.service.JwtService;
import com.yourname.tomorrowlandshop.service.RefreshTokenService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    private static final String PRODUCTS_PATH = "/products";
    private static final String API_PATH = "/api";
    private static final String ACCESS_COOKIE = "access_token";
    private static final String REFRESH_COOKIE = "refresh_token";
    private static final int ACCESS_TTL = 15 * 60;
    private static final int REFRESH_TTL = 7 * 24 * 60 * 60;

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

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

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearAuthCookies(HttpServletResponse response) {
        setCookie(response, ACCESS_COOKIE, "", 0);
        setCookie(response, REFRESH_COOKIE, "", 0);
    }

    private static String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter,
                                            LoginAuditFilter loginAuditFilter,
                                            DaoAuthenticationProvider daoAuthenticationProvider,
                                            JwtService jwtService,
                                            RefreshTokenService refreshTokenService,
                                            UserRepository userRepository) throws Exception {
        http.authenticationProvider(daoAuthenticationProvider);
        http.csrf(csrf -> csrf
                .csrfTokenRepository(new CookieCsrfTokenRepository()));
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String path = request.getRequestURI();
                    String ctx = request.getContextPath();
                    if (path.startsWith(ctx + API_PATH)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    redirectToProducts(request, response);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    String path = request.getRequestURI();
                    String ctx = request.getContextPath();
                    if (path.startsWith(ctx + API_PATH)) {
                        sendApiForbidden(response);
                        return;
                    }
                    redirectToProducts(request, response);
                }));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
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
                .successHandler((request, response, authentication) -> {
                    String username = authentication.getName();
                    String accessToken = jwtService.generateAccessToken(username);
                    String refreshToken = jwtService.generateRefreshToken(username);
                    userRepository.findByUsername(username).ifPresent(user ->
                            refreshTokenService.persist(
                                    jwtService.extractTokenId(refreshToken),
                                    user,
                                    jwtService.extractExpiration(refreshToken)
                            ));
                    setCookie(response, ACCESS_COOKIE, accessToken, ACCESS_TTL);
                    setCookie(response, REFRESH_COOKIE, refreshToken, REFRESH_TTL);
                    redirectToProducts(request, response);
                }));
        http.logout(logout -> logout
                .logoutSuccessHandler((request, response, authentication) -> {
                    String refreshToken = readCookie(request, REFRESH_COOKIE);
                    if (refreshToken != null && jwtService.validateToken(refreshToken)) {
                        refreshTokenService.revoke(jwtService.extractTokenId(refreshToken));
                    }
                    clearAuthCookies(response);
                    redirectToProducts(request, response);
                })
                .permitAll());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(loginAuditFilter, UsernamePasswordAuthenticationFilter.class);
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
