package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private HttpSession session;

    private CartService cartService;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
        cart = new Cart();
        when(session.getAttribute("CART")).thenReturn(cart);
    }

    @Test
    void shouldHandleAllCartOperations() {
        Product product = Product.builder().id(1L).price(new BigDecimal("15.00")).build();

        cartService.add(session, product, 2);
        cartService.update(session, 1L, 3);
        cartService.remove(session, 1L);
        cartService.clear(session);

        assertThat(cart.getItems()).isEmpty();
    }
}
