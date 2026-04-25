package com.yourname.tomorrowlandshop.repository;

import com.yourname.tomorrowlandshop.domain.entity.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {

    List<LoginAudit> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
