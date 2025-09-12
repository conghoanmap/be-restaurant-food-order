package com.restaurant.foodorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.restaurant.foodorder.model.Table;

@Repository
public interface TableRepo extends JpaRepository<Table, Long> {

}
