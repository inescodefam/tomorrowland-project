package com.yourname.tomorrowlandshop.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_audit")
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

    protected LoginAudit() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public LocalDateTime getLoginAt() {
        return getCreatedAt();
    }

    public static final class Builder {
        private final LoginAudit target = new LoginAudit();

        public Builder id(Long value) {
            target.id = value;
            return this;
        }

        public Builder username(String value) {
            target.username = value;
            return this;
        }

        public Builder ipAddress(String value) {
            target.ipAddress = value;
            return this;
        }

        public Builder loginAt(LocalDateTime value) {
            target.createdAt = value;
            return this;
        }

        public Builder createdAt(LocalDateTime value) {
            return loginAt(value);
        }

        public Builder success(boolean value) {
            target.success = value;
            return this;
        }

        public LoginAudit build() {
            return target;
        }
    }
}
