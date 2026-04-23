package com.bootcamp.foodorder.repository;

import com.bootcamp.foodorder.entity.Order;
import com.bootcamp.foodorder.entity.OrderStatus;
import com.bootcamp.foodorder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusAndCreatedAtBetween(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

}
