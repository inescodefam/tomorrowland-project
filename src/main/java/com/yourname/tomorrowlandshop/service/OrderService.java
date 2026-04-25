package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.exception.OrderConflictException;
import com.yourname.tomorrowlandshop.repository.OrderRepository;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;

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

    public Order placeOrder(Long userId, Long productId, int quantity, PaymentMethod paymentMethod) {
        try {
            Product product = productRepository.findById(productId).orElseThrow();
            User user = userRepository.findById(userId).orElseThrow();
            product.decrementStock(quantity);
            Order order = Order.builder().user(user).status(com.yourname.tomorrowlandshop.domain.enums.OrderStatus.PENDING).build();
            Order saved = orderRepository.save(order);
            paymentService.initiatePayment(saved);
            return saved;
        } catch (OptimisticLockException ex) {
            throw new OrderConflictException("Concurrent order conflict");
        }
    }
}
