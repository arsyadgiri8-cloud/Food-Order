package com.bootcamp.foodorder.repository;

import com.bootcamp.foodorder.entity.CartItem;
import com.bootcamp.foodorder.entity.Menu;
import com.bootcamp.foodorder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndMenu(User user, Menu menu);

    Optional<CartItem> findByIdAndUser(Long id, User user);

    void deleteByUser(User user);
}
