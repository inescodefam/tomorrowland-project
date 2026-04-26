package com.yourname.tomorrowlandshop.repository;

import com.yourname.tomorrowlandshop.domain.entity.Order;
import com.yourname.tomorrowlandshop.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from Order o left join fetch o.orderItems join fetch o.user where o.id = :id")
    Optional<Order> findDetailById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"orderItems"})
    List<Order> findByUser(User user);

    List<Order> findByUserAndCreatedAtBetween(User user, LocalDateTime from, LocalDateTime to);

    @EntityGraph(attributePaths = {"orderItems", "user"})
    List<Order> findAllByOrderByCreatedAtDesc();
}
