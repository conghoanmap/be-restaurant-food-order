package com.restaurant.foodorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.foodorder.model.DishType;

@Repository
public interface DishTypeRepo extends JpaRepository<DishType, Long> {
    boolean existsById(Long id);
}
