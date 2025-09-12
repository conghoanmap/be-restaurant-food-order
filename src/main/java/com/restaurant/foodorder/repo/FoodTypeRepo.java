package com.restaurant.foodorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.foodorder.model.FoodType;

@Repository
public interface FoodTypeRepo extends JpaRepository<FoodType, Long> {

}
