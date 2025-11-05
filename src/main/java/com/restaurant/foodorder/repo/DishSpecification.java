package com.restaurant.foodorder.repo;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.restaurant.foodorder.model.Dish;

public class DishSpecification {

    public static Specification<Dish> hasStatus(String status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Dish> hasDishTypes(List<Long> typeIds) {
        return (root, query, cb) -> {
            if (typeIds == null || typeIds.isEmpty())
                return null;
            return root.get("dishTypes").get("id").in(typeIds);
        };
    }

    public static Specification<Dish> receiverNameContains(String keyword) {
        return (root, query, cb) -> keyword == null ? null
                : cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Dish> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null)
                return null;
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
        };
    }
}
