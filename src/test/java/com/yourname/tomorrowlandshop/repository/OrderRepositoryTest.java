package com.yourname.tomorrowlandshop.repository;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.User;
import com.yourname.tomorrowlandshop.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("find orders by user")
    void shouldFindByUser() {
        User user = userRepository.save(User.builder().username("u1").email("u1@test.com").password("x").role(Role.ROLE_USER).build());
        orderRepository.save(Order.builder().user(user).createdAt(LocalDateTime.now()).build());

        assertThat(orderRepository.findByUser(user)).hasSize(1);
    }

    @Test
    @DisplayName("find orders by user in date range")
    void shouldFindByUserAndDateBetween() {
        User user = userRepository.save(User.builder().username("u2").email("u2@test.com").password("x").role(Role.ROLE_USER).build());
        LocalDateTime now = LocalDateTime.now();
        orderRepository.save(Order.builder().user(user).createdAt(now.minusHours(1)).build());

        assertThat(orderRepository.findByUserAndCreatedAtBetween(user, now.minusDays(1), now.plusDays(1))).hasSize(1);
    }

    @Test
    @DisplayName("find all orders sorted by createdAt descending")
    void shouldFindAllByOrderByCreatedAtDesc() {
        User user = userRepository.save(User.builder().username("sortUser").email("sort@test.com").password("x").role(Role.ROLE_USER).build());
        LocalDateTime now = LocalDateTime.now();
        orderRepository.save(Order.builder().user(user).createdAt(now.minusHours(2)).build());
        orderRepository.save(Order.builder().user(user).createdAt(now.minusHours(1)).build());

        assertThat(orderRepository.findAllByOrderByCreatedAtDesc())
                .hasSize(2)
                .extracting(Order::getCreatedAt)
                .isSortedAccordingTo((a, b) -> b.compareTo(a));
    }
}
