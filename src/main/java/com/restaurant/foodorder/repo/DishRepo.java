package com.restaurant.foodorder.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.foodorder.model.Dish;

@Repository
public interface DishRepo extends JpaRepository<Dish, Long> {
    List<Dish> findByDishTypes_Id(Long typeId); // Lấy món ăn theo loại món

    List<Dish> findByDishTypes_IdIn(List<Long> typeIds); // Lấy món ăn theo nhiều loại món
}
