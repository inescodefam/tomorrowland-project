package com.yourname.tomorrowlandshop.integration;

import com.yourname.tomorrowlandshop.domain.entity.Cart;
import com.yourname.tomorrowlandshop.domain.entity.Category;
import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.Product;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.Role;
import com.yourname.tomorrowlandshop.repository.OrderRepository;
import com.yourname.tomorrowlandshop.repository.ProductRepository;
import com.yourname.tomorrowlandshop.repository.CategoryRepository;
import com.yourname.tomorrowlandshop.repository.UserRepository;
import com.yourname.tomorrowlandshop.domain.enums.PaymentMethod;
import com.yourname.tomorrowlandshop.domain.exception.OrderConflictException;
import com.yourname.tomorrowlandshop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
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
import java.math.BigDecimal;

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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private long seededProductId;
    private List<Long> seededBuyerIds;
    private Product seededProduct;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        Category category = categoryRepository.save(Category.builder().name("Tickets").description("Festival tickets").build());
        Product product = productRepository.save(Product.builder()
                .name("Full Madness Pass")
                .description("Weekend ticket")
                .price(new BigDecimal("299.99"))
                .stock(1)
                .category(category)
                .build());
        seededProduct = product;
        seededProductId = product.getId();
        seededBuyerIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            User saved = userRepository.save(User.builder()
                    .username("buyer" + i)
                    .email("buyer" + i + "@test.com")
                    .password("x")
                    .role(Role.ROLE_USER)
                    .build());
            seededBuyerIds.add(saved.getId());
        }
    }

    @Test
    @DisplayName("10 buyers race for last ticket: exactly 1 success and 9 conflicts")
    void shouldAllowOnlyOneSuccessfulOrder() throws Exception {
        long productId = seededProductId;
        List<Long> buyerIds = seededBuyerIds;
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<String>> tasks = new ArrayList<>();

        for (Long buyerId : buyerIds) {
            tasks.add(() -> {
                start.await();
                try {
                    Cart cart = new Cart();
                    cart.addItem(seededProduct, 1);
                    Order order = orderService.placeOrder(buyerId, cart, PaymentMethod.PAYPAL);
                    return "SUCCESS:" + order.getId();
                } catch (OrderConflictException ex) {
                    return "CONFLICT";
                } catch (Exception ex) {
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
