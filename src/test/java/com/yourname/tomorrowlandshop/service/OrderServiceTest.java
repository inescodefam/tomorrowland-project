package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.exception.InsufficientStockException;
import com.yourname.tomorrowlandshop.domain.exception.OrderConflictException;
import com.yourname.tomorrowlandshop.repository.OrderRepository;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder_success() {
        Product product = Product.builder().id(1L).name("P").stock(5).price(new java.math.BigDecimal("10")).build();
        Cart cart = new Cart();
        cart.addItem(product, 1);
        when(productRepository.findByIdWithPessimisticLock(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        orderService.placeOrder(1L, cart, PaymentMethod.PAYPAL);
    }

    @Test
    void placeOrder_concurrentConflict() {
        Cart cart = new Cart();
        cart.addItem(Product.builder().id(1L).name("P").price(java.math.BigDecimal.ONE).stock(1).build(), 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(productRepository.findByIdWithPessimisticLock(1L)).thenThrow(new OptimisticLockException("conflict"));
        assertThatThrownBy(() -> orderService.placeOrder(1L, cart, PaymentMethod.PAYPAL))
                .isInstanceOf(OrderConflictException.class);
    }

    @Test
    void placeOrder_insufficientStock() {
        Product product = Product.builder().id(1L).name("P").stock(0).price(java.math.BigDecimal.ONE).build();
        Cart cart = new Cart();
        cart.addItem(product, 1);
        when(productRepository.findByIdWithPessimisticLock(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));

        assertThatThrownBy(() -> orderService.placeOrder(1L, cart, PaymentMethod.CASH_ON_DELIVERY))
                .isInstanceOf(InsufficientStockException.class);
    }
}
