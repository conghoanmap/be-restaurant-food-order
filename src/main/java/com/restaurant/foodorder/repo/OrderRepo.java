package com.restaurant.foodorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.foodorder.model.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, String> {

}
