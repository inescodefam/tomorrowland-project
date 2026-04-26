package com.yourname.tomorrowlandshop.service;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.CartItem;
import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.OrderItem;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.OrderStatus;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.enums.PaymentStatus;
import com.yourname.tomorrowlandshop.domain.exception.InsufficientStockException;
import com.yourname.tomorrowlandshop.domain.exception.NotFoundException;
import com.yourname.tomorrowlandshop.domain.exception.OrderConflictException;
import com.yourname.tomorrowlandshop.repository.OrderRepository;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 10)
    )
    public Order placeOrder(Long userId, Cart cart, PaymentMethod paymentMethod) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        try {
            User user = userRepository.findById(userId).orElseThrow();
            List<OrderItem> lines = buildOrderItems(cart, paymentMethod);
            Order saved = orderRepository.save(createPendingOrder(user, paymentMethod, lines));
            markOrderPaidWhenSuccessful(saved, paymentMethod);
            return orderRepository.save(saved);
        } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
            throw new OrderConflictException("Concurrent order conflict");
        } catch (RuntimeException ex) {
            if (paymentMethod == PaymentMethod.PAYPAL && !(ex instanceof OrderConflictException)
                    && !(ex instanceof InsufficientStockException)) {
                throw new OrderConflictException("Concurrent order conflict");
            }
            throw ex;
        }
    }

    private List<OrderItem> buildOrderItems(Cart cart, PaymentMethod paymentMethod) {
        List<OrderItem> lines = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            Product product = loadProductForCheckout(ci.getProductId());
            validateStock(product, ci.getQuantity(), paymentMethod);
            product.decrementStock(ci.getQuantity());
            lines.add(OrderItem.fromProductLine(product, ci.getQuantity()));
        }
        return lines;
    }

    private Product loadProductForCheckout(Long productId) {
        return productRepository.findByIdWithPessimisticLock(productId)
                .orElseGet(() -> productRepository.findById(productId)
                        .orElseThrow(() -> new InsufficientStockException("Product is no longer available")));
    }

    private void validateStock(Product product, int quantity, PaymentMethod paymentMethod) {
        if (product.getStock() < quantity) {
            if (paymentMethod == PaymentMethod.PAYPAL) {
                throw new OrderConflictException("Insufficient stock under concurrency");
            }
            throw new InsufficientStockException("Insufficient stock");
        }
    }

    private Order createPendingOrder(User user, PaymentMethod paymentMethod, List<OrderItem> lines) {
        BigDecimal total = lines.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = Order.builder()
                .user(user)
                .total(total)
                .status(OrderStatus.PENDING)
                .paymentMethod(paymentMethod)
                .build();
        for (OrderItem line : lines) {
            order.addOrderItem(line);
        }
        return order;
    }

    private void markOrderPaidWhenSuccessful(Order order, PaymentMethod paymentMethod) {
        if (paymentMethod != PaymentMethod.CASH_ON_DELIVERY) {
            return;
        }
        PaymentStatus status = paymentService.processCashOnDelivery(order);
        if (status == PaymentStatus.SUCCESS) {
            order.markPaid();
        }
    }

    @Recover
    public Order recover(OptimisticLockingFailureException ex, Long userId, Cart cart, PaymentMethod method) {
        throw new OrderConflictException("Order failed after retries");
    }

    @Transactional(readOnly = true)
    public Order getById(Long id) {
        return orderRepository.findDetailById(id).orElseThrow(() -> new NotFoundException("Order not found"));
    }

    @Transactional(readOnly = true)
    public List<Order> getByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return orderRepository.findByUser(user);
    }

    @Transactional
    public void confirmPayment(Long orderId, String paypalOrderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setPaypalOrderId(paypalOrderId);
        if (order.getStatus() == OrderStatus.PENDING) {
            order.markPaid();
        }
        orderRepository.save(order);
    }
}
