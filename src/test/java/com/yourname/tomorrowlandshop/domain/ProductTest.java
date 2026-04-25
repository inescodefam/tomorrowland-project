package com.yourname.tomorrowlandshop.domain;

import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("version increments after update")
    void shouldIncrementVersionOnUpdate() {
        Product product = Product.builder().name("Pass").price(new BigDecimal("99.00")).stock(10).build();
        Long initialVersion = product.getVersion();

        product.setPrice(new BigDecimal("109.00"));

        assertThat(product.getVersion()).isNotEqualTo(initialVersion);
    }

    @Test
    @DisplayName("decrement stock by requested quantity")
    void shouldDecrementStock() {
        Product product = Product.builder().name("Pass").price(new BigDecimal("99.00")).stock(10).build();

        product.decrementStock(3);

        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("throw when requested quantity exceeds stock")
    void shouldThrowInsufficientStockException() {
        Product product = Product.builder().name("Pass").price(new BigDecimal("99.00")).stock(1).build();

        assertThatThrownBy(() -> product.decrementStock(2)).isInstanceOf(InsufficientStockException.class);
    }
}
