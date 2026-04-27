package com.yourname.tomorrowlandshop.domain.entity;

import com.yourname.tomorrowlandshop.domain.enums.OrderStatus;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    @Column(name = "paypal_order_id")
    private String paypalOrderId;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

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
        order.orderItems = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProductId());
            oi.setProductName(ci.getProductName());
            oi.setUnitPrice(ci.getPrice());
            oi.setQuantity(ci.getQuantity());
            oi.setOrder(order);
            order.orderItems.add(oi);
        }
        return order;
    }

    public void addOrderItem(OrderItem item) {
        item.setOrder(this);
        orderItems.add(item);
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void markShipped() {
        this.status = OrderStatus.SHIPPED;
    }

    public void markCancelled() {
        this.status = OrderStatus.CANCELLED;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaypalOrderId() {
        return paypalOrderId;
    }

    public void setPaypalOrderId(String paypalOrderId) {
        this.paypalOrderId = paypalOrderId;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
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

        public Builder paymentMethod(PaymentMethod value) {
            target.paymentMethod = value;
            return this;
        }

        public Builder paypalOrderId(String value) {
            target.paypalOrderId = value;
            return this;
        }

        public Builder orderItems(List<OrderItem> value) {
            target.orderItems = new ArrayList<>(value);
            return this;
        }

        public Order build() {
            if (target.status == null) {
                target.status = OrderStatus.PENDING;
            }
            if (target.createdAt == null) {
                target.createdAt = LocalDateTime.now();
            }
            if (target.orderItems == null) {
                target.orderItems = new ArrayList<>();
            }
            return target;
        }
    }
}
