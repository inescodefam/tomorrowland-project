package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private HttpSession session;
    @Mock
    private ProductRepository productRepository;

    private CartService cartService;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartService = new CartService(productRepository);
        cart = new Cart();
        when(session.getAttribute(CartService.SESSION_CART_KEY)).thenReturn(cart);
    }

    @Test
    void shouldHandleAllCartOperations() {
        Product product = Product.builder().id(1L).name("P").price(new BigDecimal("15.00")).stock(10).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.addItem(session, 1L, 2);
        cartService.updateQuantity(session, 1L, 3);
        cartService.removeItem(session, 1L);
        cartService.clearCart(session);

        assertThat(cart.getItems()).isEmpty();
        verify(productRepository, org.mockito.Mockito.times(2)).findById(1L);
    }

    @Test
    void reconcileRemovesSoldOutAndCapsQuantity() {
        cart.addItem(Product.builder().id(1L).name("A").price(new BigDecimal("10")).stock(2).build(), 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(
                Product.builder().id(1L).name("A").price(new BigDecimal("10")).stock(1).build()));

        cartService.reconcileCartWithStock(session);

        assertThat(cart.getItems()).singleElement().satisfies(i -> assertThat(i.getQuantity()).isEqualTo(1));
    }

    @Test
    void reconcileRemovesLineWhenStockZero() {
        cart.addItem(Product.builder().id(2L).name("B").price(BigDecimal.ONE).stock(0).build(), 1);
        when(productRepository.findById(2L)).thenReturn(Optional.of(
                Product.builder().id(2L).name("B").price(BigDecimal.ONE).stock(0).build()));

        cartService.reconcileCartWithStock(session);

        assertThat(cart.getItems()).isEmpty();
    }
}
