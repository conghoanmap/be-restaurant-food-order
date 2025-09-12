package com.restaurant.foodorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.foodorder.model.Booking;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

}
