package com.restaurant.foodorder.repo;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;

import com.restaurant.foodorder.model.Order;

public class OrderSpecification {
    public static Specification<Order> hasStatus(String status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> hasPaid(Boolean paid) {
        return (root, query, cb) -> paid == null ? null : cb.equal(root.get("paid"), paid);
    }

    public static Specification<Order> receiverNameContains(String keyword) {
        return (root, query, cb) -> keyword == null ? null
                : cb.like(cb.lower(root.get("id")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Order> fromDate(LocalDate fromDate) {
        return (root, query, cb) -> {
            if (fromDate == null)
                return null;
            return cb.greaterThanOrEqualTo(root.get("orderDate"), fromDate.atStartOfDay());
        };
    }

    public static Specification<Order> toDate(LocalDate toDate) {
        return (root, query, cb) -> {
            if (toDate == null)
                return null;
            return cb.lessThanOrEqualTo(root.get("orderDate"), toDate.atTime(LocalTime.MAX));
        };
    }
}
