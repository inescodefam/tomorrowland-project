package com.yourname.tomorrowlandshop.repository;

import com.yourname.tomorrowlandshop.domain.entity.LoginAudit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LoginAuditRepositoryTest {

    @Autowired
    private LoginAuditRepository loginAuditRepository;

    @Test
    @DisplayName("save login audit entry")
    void shouldSaveEntry() {
        LoginAudit saved = loginAuditRepository.save(LoginAudit.builder()
                .username("user1")
                .ipAddress("127.0.0.1")
                .createdAt(LocalDateTime.now())
                .success(true)
                .build());
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("find login audit entries by date range")
    void shouldFindByDateRange() {
        LocalDateTime now = LocalDateTime.now();
        loginAuditRepository.save(LoginAudit.builder()
                .username("user2")
                .ipAddress("127.0.0.1")
                .createdAt(now)
                .success(false)
                .build());

        assertThat(loginAuditRepository.findByCreatedAtBetween(now.minusDays(1), now.plusDays(1))).hasSize(1);
    }
}
