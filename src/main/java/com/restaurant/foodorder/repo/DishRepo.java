package com.restaurant.foodorder.repo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.restaurant.foodorder.model.Dish;

@Repository
public interface DishRepo extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {
    Page<Dish> findByDishTypes_Id(Long typeId, Pageable pageable); // Lấy món ăn theo loại món

    Page<Dish> findByDishTypes_IdIn(List<Long> typeIds, Pageable pageable); // Lấy món ăn theo nhiều loại món
}
