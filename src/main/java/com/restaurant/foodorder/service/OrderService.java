package com.restaurant.foodorder.service;

import com.restaurant.foodorder.auth.model.AppUser;
import com.restaurant.foodorder.auth.repo.AppUserRepository;
import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.OrderItemReq;
import com.restaurant.foodorder.dto.OrderMessage;
import com.restaurant.foodorder.dto.PageResponse;
import com.restaurant.foodorder.enumm.OrderStatus;
import com.restaurant.foodorder.enumm.PaymentMethod;
import com.restaurant.foodorder.model.Dish;
import com.restaurant.foodorder.model.Order;
import com.restaurant.foodorder.model.OrderItem;
import com.restaurant.foodorder.model.temp_redis.TempCart;
import com.restaurant.foodorder.repo.DishRepo;
import com.restaurant.foodorder.repo.OrderRepo;
import com.restaurant.foodorder.repo.OrderSpecification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderService {
    private final AppUserRepository appUserRepository;
    private final OrderRepo orderRepo;
    private final DishRepo dishRepo;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private static final double SHIPPING_FEE = 30000;

    public OrderService(OrderRepo orderRepo, DishRepo dishRepo, RedisTemplate<String, Object> redisTemplate,
            SimpMessagingTemplate messagingTemplate, AppUserRepository appUserRepository) {
        this.orderRepo = orderRepo;
        this.dishRepo = dishRepo;
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public Order createEmptyOrder() {
        Order order = new Order();
        return orderRepo.save(order);
    }

    @RabbitListener(queues = "orderQueue")
    public void processOrder(OrderMessage orderMessage) {
        log.info("New order message received from RabbitMQ!!!");
        Optional<Order> existingOrder = orderRepo.findById(orderMessage.getOrderId());
        if (!existingOrder.isPresent()) {
            log.warn("Order with id {} not found. Skipping processing.", orderMessage.getOrderId());
            return;
        }
        Order order = existingOrder.get();
        order.setId(orderMessage.getOrderId());
        order.setReceiverName(orderMessage.getReceiverName());
        order.setReceiverAddress(orderMessage.getReceiverAddress());
        order.setReceiverPhone(orderMessage.getReceiverPhone());
        order.setNote(orderMessage.getNote());
        order.setPaymentMethod(PaymentMethod.valueOf(orderMessage.getPaymentMethod()));

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;
        for (OrderItemReq orderItemReq : orderMessage.getOrderItemReqs()) {

            OrderItem orderItem = new OrderItem();
            // Nếu dishId chứa kí tự '-' thì lấy phần đầu chyển sang kiểu Long để tìm ra
            // dish
            String s = orderItemReq.getDishId();
            int index = s.indexOf("-");
            String dishId = (index != -1) ? s.substring(0, index) : s;

            Dish dish = dishRepo.findById(Long.parseLong(dishId)).orElse(null);
            orderItem.setDish(dish);
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            orderItem.setDishName(orderItemReq.getDishName());
            orderItem.setQuantity(orderItemReq.getQuantity());
            orderItem.setPrice(orderItemReq.getPrice() - dish.getDiscount());
            orderItem.setTotalPrice((orderItemReq.getPrice() - dish.getDiscount()) *
                    orderItemReq.getQuantity());
            totalPrice += (orderItemReq.getPrice() - dish.getDiscount()) * orderItemReq.getQuantity();
        }
        order.setOrderItems(orderItems);
        order.setPaid(false); // Chưa thanh toán

        Optional<AppUser> user = appUserRepository.findByEmail(orderMessage.getEmail());
        if (user.isEmpty()) {
            log.error("User with email {} not found. Order cannot be processed.", orderMessage.getEmail());
            return;
        }

        order.setAppUser(user.get());
        order.setTotalPrice(totalPrice + SHIPPING_FEE);
        Order createdOrder = orderRepo.save(order);
        log.info("Order created successfully, orderId: {}", createdOrder.getId());
        // Gửi tin nhắn cho admin
        messagingTemplate.convertAndSend("/topic/admin/orders", createdOrder);
        // Xóa trong giỏ hàng
        TempCart existingCart = (TempCart) redisTemplate.opsForValue()
                .get("cart:" + user.get().getUserId());
        if (existingCart != null) {
            orderMessage.getOrderItemReqs().forEach(orderItem -> {
                existingCart.getItems().removeIf(item -> item.getDishId().equals(orderItem.getDishId()));
            });
            log.info("After deleted: {}", existingCart);
            redisTemplate.opsForValue().set("cart:" +
                    user.get().getUserId(), existingCart, 7,
                    TimeUnit.DAYS);
        } else {
            log.warn("User not {} found", user.get().getUserId());
        }
    }

    // Cập nhật trạng thái đã thanh toán
    public APIResponse<Order> updatePaymentStatus(String orderId, boolean isPaid) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return new APIResponse<>(404, "Order not found", null);
        }
        order.setPaid(isPaid);
        Order updatedOrder = orderRepo.save(order);
        log.info("Order payment status updated, orderId: {}, isPaid: {}", orderId, isPaid);
        return new APIResponse<>(200, "Payment status updated successfully", updatedOrder);
    }

    public APIResponse<PageResponse<Order>> getOrders(String status, Boolean paid, String keyword, LocalDate fromDate,
            LocalDate toDate, Pageable pageable) {
        Specification<Order> spec = Specification
                .where(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasPaid(paid))
                .and(OrderSpecification.receiverNameContains(keyword))
                .and(OrderSpecification.fromDate(fromDate))
                .and(OrderSpecification.toDate(toDate));
        Page<Order> orders = orderRepo.findAll(spec, pageable);
        return new APIResponse<>(200, "Orders retrieved successfully", new PageResponse<>(
                orders.getContent(),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()));
    }

    public APIResponse<Order> updateOrderStatus(String orderId, String status) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return new APIResponse<>(404, "Order not found", null);
        }
        try {
            order.setStatus(OrderStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            return new APIResponse<>(400, "Invalid order status", null);
        }
        Order updatedOrder = orderRepo.save(order);
        log.info("Order status updated, orderId: {}, status: {}", orderId, status);
        return new APIResponse<>(200, "Order status updated successfully", updatedOrder);
    }

    public APIResponse<Order> cancelOrder(String orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return new APIResponse<>(404, "Order not found", null);
        }
        if (order.isPaid()) {
            // Không cho hủy nếu đã thanh toán
            return new APIResponse<>(400, "Paid orders cannot be canceled", null);
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            // Chỉ cho phép hủy đơn hàng đang ở trạng thái PENDING hoặc CONFIRMED
            return new APIResponse<>(400, "Only orders with status PENDING or CONFIRMED can be canceled", null);
        }
        order.setStatus(OrderStatus.CANCELED);
        Order updatedOrder = orderRepo.save(order);
        log.info("Order canceled successfully, orderId: {}", orderId);
        return new APIResponse<>(200, "Order canceled successfully", updatedOrder);
    }

}
