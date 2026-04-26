package com.yourname.tomorrowlandshop.domain;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartTest {

    @Test
    @DisplayName("add item and calculate total")
    void shouldAddItemAndCalculateTotal() {
        Cart cart = new Cart();
        Product pass = Product.builder().id(1L).name("Pass").price(new BigDecimal("299.99")).build();

        cart.addItem(pass, 2);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getTotal()).isEqualByComparingTo("599.98");
    }

    @Test
    @DisplayName("update quantity")
    void shouldUpdateQuantity() {
        Cart cart = new Cart();
        Product merch = Product.builder().id(2L).name("Merch").price(new BigDecimal("10.00")).build();
        cart.addItem(merch, 1);

        cart.updateQuantity(2L, 3);

        assertThat(cart.getItems()).singleElement()
                .satisfies(item -> {
                    assertThat(item.getProductId()).isEqualTo(2L);
                    assertThat(item.getQuantity()).isEqualTo(3);
                });
    }

    @Test
    @DisplayName("remove item")
    void shouldRemoveItem() {
        Cart cart = new Cart();
        Product merch = Product.builder().id(2L).name("Merch").price(new BigDecimal("10.00")).build();
        cart.addItem(merch, 1);

        cart.removeItem(2L);

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("clear cart")
    void shouldClearCart() {
        Cart cart = new Cart();
        cart.addItem(Product.builder().id(1L).name("A").price(new BigDecimal("1.00")).build(), 1);
        cart.addItem(Product.builder().id(2L).name("B").price(new BigDecimal("2.00")).build(), 1);

        cart.clear();

        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isZero();
    }
}
