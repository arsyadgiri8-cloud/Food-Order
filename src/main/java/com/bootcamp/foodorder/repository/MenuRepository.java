package com.bootcamp.foodorder.repository;

import com.bootcamp.foodorder.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Page<Menu> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Menu> findByCategoryContainingIgnoreCase(String category, Pageable pageable);

    Page<Menu> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(
            String name,
            String category,
            Pageable pageable
    );
}
