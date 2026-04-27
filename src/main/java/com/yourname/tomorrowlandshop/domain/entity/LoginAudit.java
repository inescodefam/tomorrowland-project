package com.yourname.tomorrowlandshop.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_audit")
@Getter
@Builder(builderMethodName = "internalBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String ipAddress;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private boolean success;

    public static LoginAuditBuilder builder() {
        return internalBuilder();
    }

    public LocalDateTime getLoginAt() {
        return getCreatedAt();
    }

    public static class LoginAuditBuilder {
        public LoginAuditBuilder loginAt(LocalDateTime value) {
            this.createdAt = value;
            return this;
        }
    }
}
