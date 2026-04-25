package com.yourname.tomorrowlandshop.domain.entity;

import com.yourname.tomorrowlandshop.domain.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(precision = 12, scale = 2)
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Transient
    private List<CartItem> items;

    protected Order() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Order fromCart(Long userId, Cart cart) {
        Order order = new Order();
        order.user = User.builder().id(userId).build();
        order.total = cart.getTotal();
        order.status = OrderStatus.PENDING;
        order.createdAt = LocalDateTime.now();
        order.items = new ArrayList<>(cart.getItems().values());
        return order;
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void markShipped() {
        this.status = OrderStatus.SHIPPED;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<CartItem> getItems() {
        return items == null ? List.of() : List.copyOf(items);
    }

    public static final class Builder {
        private final Order target = new Order();

        public Builder id(Long value) {
            target.id = value;
            return this;
        }

        public Builder user(User value) {
            target.user = value;
            return this;
        }

        public Builder total(BigDecimal value) {
            target.total = value;
            return this;
        }

        public Builder status(OrderStatus value) {
            target.status = value;
            return this;
        }

        public Builder createdAt(LocalDateTime value) {
            target.createdAt = value;
            return this;
        }

        public Builder items(List<CartItem> value) {
            target.items = value;
            return this;
        }

        public Order build() {
            if (target.status == null) {
                target.status = OrderStatus.PENDING;
            }
            if (target.createdAt == null) {
                target.createdAt = LocalDateTime.now();
            }
            return target;
        }
    }
}
