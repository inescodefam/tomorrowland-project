package com.yourname.tomorrowlandshop.security;

import com.yourname.tomorrowlandshop.domain.entity.LoginAudit;
import com.yourname.tomorrowlandshop.repository.LoginAuditRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoginSuccessListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private final LoginAuditRepository loginAuditRepository;

    public LoginSuccessListener(LoginAuditRepository loginAuditRepository) {
        this.loginAuditRepository = loginAuditRepository;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        String ip = "unknown";
        if (auth.getDetails() instanceof WebAuthenticationDetails details) {
            ip = details.getRemoteAddress();
        }
        loginAuditRepository.save(LoginAudit.builder()
                .username(auth.getName())
                .ipAddress(ip)
                .createdAt(LocalDateTime.now())
                .success(true)
                .build());
    }
}
