package com.yourname.tomorrowlandshop.integration;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.repository.OrderRepository;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.exception.OrderConflictException;
import com.yourname.tomorrowlandshop.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ParallelBookingIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("10 buyers race for last ticket: exactly 1 success and 9 conflicts")
    void shouldAllowOnlyOneSuccessfulOrder() throws Exception {
        long productId = 1L;
        List<Long> buyerIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<String>> tasks = new ArrayList<>();

        for (Long buyerId : buyerIds) {
            tasks.add(() -> {
                start.await();
                try {
                    Order order = orderService.placeOrder(buyerId, productId, 1, PaymentMethod.PAYPAL);
                    return "SUCCESS:" + order.getId();
                } catch (OrderConflictException ex) {
                    return "CONFLICT";
                }
            });
        }

        List<Future<String>> futures = new ArrayList<>();
        for (Callable<String> task : tasks) {
            futures.add(executor.submit(task));
        }
        start.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        long success = futures.stream().map(this::safeGet).filter(r -> r.startsWith("SUCCESS")).count();
        long conflict = futures.stream().map(this::safeGet).filter("CONFLICT"::equals).count();

        assertThat(success).isEqualTo(1);
        assertThat(conflict).isEqualTo(9);
        assertThat(productRepository.findById(productId).orElseThrow().getStock()).isZero();
        assertThat(orderRepository.findAll()).hasSize(1);
        assertThat(orderRepository.findAll().stream().map(Order::getUser).distinct().count()).isEqualTo(1);
    }

    private String safeGet(Future<String> future) {
        try {
            return future.get();
        } catch (Exception e) {
            return "ERROR";
        }
    }
}
