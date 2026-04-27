package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.domain.entity.LoginAudit;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDateTime;

public class LoginAuditFilter implements Filter {

    private final LoginAuditRepository loginAuditRepository;

    public LoginAuditFilter(LoginAuditRepository loginAuditRepository) {
        this.loginAuditRepository = loginAuditRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean isLoginAttempt = "/login".equals(httpRequest.getRequestURI())
                && "POST".equalsIgnoreCase(httpRequest.getMethod());

        chain.doFilter(request, response);

        if (isLoginAttempt && loginAuditRepository != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean failed = auth == null || !auth.isAuthenticated()
                    || "anonymousUser".equals(auth.getPrincipal());
            if (failed) {
                String username = httpRequest.getParameter("username");
                loginAuditRepository.save(LoginAudit.builder()
                        .username(username != null ? username : "unknown")
                        .ipAddress(httpRequest.getRemoteAddr())
                        .createdAt(LocalDateTime.now())
                        .success(false)
                        .build());
            }
        }
    }
}
