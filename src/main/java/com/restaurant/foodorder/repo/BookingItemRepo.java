package com.restaurant.foodorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.foodorder.model.BookingItem;

@Repository
public interface BookingItemRepo extends JpaRepository<BookingItem, Long> {

}
