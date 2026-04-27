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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    @Column(name = "paypal_order_id")
    private String paypalOrderId;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

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

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaypalOrderId(String paypalOrderId) {
        this.paypalOrderId = paypalOrderId;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public static class OrderBuilder {
        public OrderBuilder orderItems(List<OrderItem> value) {
            this.orderItems = new ArrayList<>(value);
            return this;
        }
    }

}
