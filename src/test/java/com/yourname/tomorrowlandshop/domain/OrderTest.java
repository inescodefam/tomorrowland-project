package com.yourname.tomorrowlandshop.domain;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("create order from cart and keep total")
    void shouldCreateFromCart() {
        Cart cart = new Cart();
        cart.addItem(Product.builder().id(1L).name("Pass").price(new BigDecimal("100.00")).build(), 2);

        Order order = Order.fromCart(1L, cart);

        assertThat(order.getTotal()).isEqualByComparingTo("200.00");
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("status transitions pending to paid to shipped")
    void shouldTransitionStatus() {
        Order order = Order.builder().status(OrderStatus.PENDING).build();

        order.markPaid();
        order.markShipped();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }
}
