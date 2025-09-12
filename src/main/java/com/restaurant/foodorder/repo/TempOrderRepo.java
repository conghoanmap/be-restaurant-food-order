package com.restaurant.foodorder.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.foodorder.model.temp_redis.TempOrder;

@Repository
public interface TempOrderRepo extends CrudRepository<TempOrder, Long> {

}
