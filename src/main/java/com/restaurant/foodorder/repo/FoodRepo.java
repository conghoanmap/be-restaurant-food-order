package com.restaurant.foodorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.restaurant.foodorder.model.Food;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {

}
