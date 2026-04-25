package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.domain.entity.LoginAudit;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if ("/login".equals(httpRequest.getRequestURI()) && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && loginAuditRepository != null) {
                loginAuditRepository.save(LoginAudit.builder()
                        .username(authentication.getName())
                        .ipAddress(httpRequest.getRemoteAddr())
                        .createdAt(LocalDateTime.now())
                        .success(true)
                        .build());
                httpResponse.sendRedirect("/");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
