package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.enums.OrderStatus;
import com.yourname.tomorrowlandshop.domain.exception.InsufficientStockException;
import com.yourname.tomorrowlandshop.domain.exception.OrderConflictException;
import com.yourname.tomorrowlandshop.repository.OrderRepository;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository,
                        PaymentService paymentService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 10)
    )
    public Order placeOrder(Long userId, Long productId, int quantity, PaymentMethod paymentMethod) {
        try {
            Product product = productRepository.findByIdWithPessimisticLock(productId)
                    .orElseGet(() -> productRepository.findById(productId).orElseThrow());
            User user = userRepository.findById(userId).orElseThrow();
            if (product.getStock() < quantity) {
                throw new InsufficientStockException("Insufficient stock");
            }
            product.decrementStock(quantity);
            Order order = Order.builder().user(user).status(OrderStatus.PENDING).build();
            Order saved = orderRepository.save(order);
            paymentService.initiatePayment(saved);
            return saved;
        } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
            throw new OrderConflictException("Concurrent order conflict");
        }
    }

    @Recover
    public Order recover(OptimisticLockingFailureException ex, Long userId, Long productId, int quantity, PaymentMethod method) {
        throw new OrderConflictException("Order failed after retries");
    }
}
