package com.yourname.tomorrowlandshop.domain.entity;

import com.yourname.tomorrowlandshop.domain.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() {
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public static final class Builder {
        private final User target = new User();

        public Builder id(Long value) {
            target.id = value;
            return this;
        }

        public Builder username(String value) {
            target.username = value;
            return this;
        }

        public Builder email(String value) {
            target.email = value;
            return this;
        }

        public Builder password(String value) {
            target.password = value;
            return this;
        }

        public Builder role(Role value) {
            target.role = value;
            return this;
        }

        public User build() {
            return target;
        }
    }
}
