package com.yourname.tomorrowlandshop.repository;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByUserAndCreatedAtBetween(User user, LocalDateTime from, LocalDateTime to);

    List<Order> findAllByOrderByCreatedAtDesc();
}
