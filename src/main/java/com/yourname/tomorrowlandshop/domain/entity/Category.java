package com.yourname.tomorrowlandshop.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String description;

    protected Category() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static final class Builder {
        private final Category target = new Category();

        public Builder id(Long value) {
            target.id = value;
            return this;
        }

        public Builder name(String value) {
            target.name = value;
            return this;
        }

        public Builder description(String value) {
            target.description = value;
            return this;
        }

        public Category build() {
            return target;
        }
    }
}
