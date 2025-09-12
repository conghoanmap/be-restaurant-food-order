package com.restaurant.foodorder.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.TableAndStatus;
import com.restaurant.foodorder.enumm.PaymentMethod;
import com.restaurant.foodorder.model.Order;
import com.restaurant.foodorder.model.OrderItem;
import com.restaurant.foodorder.model.Table;
import com.restaurant.foodorder.model.temp_redis.TempOrder;
import com.restaurant.foodorder.model.temp_redis.TempOrderItem;
import com.restaurant.foodorder.repo.FoodRepo;
import com.restaurant.foodorder.repo.OrderRepo;
import com.restaurant.foodorder.repo.TableRepo;
import com.restaurant.foodorder.repo.TempOrderRepo;

@Service
public class OrderService {

    private final TempOrderRepo TempOrderRepo;
    // Khai báo lớp để gửi message qua WebSocket nếu cần
    private final SimpMessagingTemplate messagingTemplate;
    private final TableRepo tableRepo;
    private final FoodRepo foodRepo;
    private final OrderRepo orderRepo;

    public OrderService(TempOrderRepo TempOrderRepo, SimpMessagingTemplate messagingTemplate, TableRepo tableRepo,
            FoodRepo foodRepo, OrderRepo orderRepo) {
        this.TempOrderRepo = TempOrderRepo;
        this.messagingTemplate = messagingTemplate;
        this.tableRepo = tableRepo;
        this.foodRepo = foodRepo;
        this.orderRepo = orderRepo;
    }

    @Transactional
    public APIResponse<TempOrder> createTempOrder(Long TableId, List<TempOrderItem> items) {
        TempOrder tempOrder = new TempOrder();
        tempOrder.setTableId(TableId);
        double totalPrice = items.stream().mapToDouble(TempOrderItem::getPrice).sum();
        tempOrder.setItems(items);
        tempOrder.setTotalPrice(totalPrice);
        TempOrder savedOrder = TempOrderRepo.save(tempOrder);
        TableAndStatus tableAndStatus = new TableAndStatus(TableId, "Occupied", totalPrice);
        messagingTemplate.convertAndSend("/topic/temp-orders", tableAndStatus); // Gửi thông báo qua WebSocket để cập
                                                                                // nhật UI
        return new APIResponse<>(200, "Temporary order created successfullyyyyy", savedOrder);
    }

    public APIResponse<TempOrder> updateTempOrder(Long tableId, List<TempOrderItem> items) {
        TempOrder existingOrder = TempOrderRepo.findById(tableId).orElse(null);
        if (existingOrder == null) {
            return new APIResponse<>(404, "Temporary order not found", null);
        }
        double totalPrice = items.stream().mapToDouble(TempOrderItem::getPrice).sum();
        existingOrder.setItems(items);
        existingOrder.setTotalPrice(totalPrice);
        TempOrder updatedOrder = TempOrderRepo.save(existingOrder);
        TableAndStatus tableAndStatus = new TableAndStatus(tableId, "Occupied", totalPrice);
        messagingTemplate.convertAndSend("/topic/temp-orders", tableAndStatus); // Gửi thông báo qua WebSocket để cập
                                                                                // nhật UI
        return new APIResponse<>(200, "Temporary order updated successfully", updatedOrder);
    }

    public APIResponse<TempOrder> getTempOrderByTableId(Long tableId) {
        TempOrder tempOrder = TempOrderRepo.findById(tableId).orElse(null);
        if (tempOrder == null) {
            return new APIResponse<>(404, "Temporary order not found", null);
        }
        return new APIResponse<>(200, "Temporary order retrieved successfully", tempOrder);
    }

    @Transactional
    public APIResponse<TempOrder> closeAndSaveOrder(Long tableId, String paymentMethod) {
        TempOrder tempOrder = TempOrderRepo.findById(tableId).orElse(null);
        if (tempOrder == null) {
            return new APIResponse<>(404, "Temporary order not found", null);
        }
        // Thêm logic lưu order vào db nếu cần
        Order order = new Order();
        Table table = tableRepo.findById(tableId).orElse(null);
        if (table == null) {
            return new APIResponse<>(404, "Table not found", null);
        }
        order.setTable(table);
        order.setTotalPrice(tempOrder.getTotalPrice());
        List<OrderItem> orderItems = tempOrder.getItems().stream().map(tempItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(foodRepo.findById(tempItem.getFoodId()).orElse(null));
            orderItem.setQuantity(tempItem.getQuantity());
            orderItem.setPrice(tempItem.getPrice());
            orderItem.setOrder(order);
            orderItem.setNote(tempItem.getNote());
            return orderItem;
        }).toList();
        order.setOrderItems(orderItems);
        order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        order.setCreatedAt(LocalDateTime.now());
        orderRepo.save(order);
        // Gửi thông báo qua WebSocket
        TableAndStatus tableAndStatus = new TableAndStatus(tableId, "Empty", 0);
        messagingTemplate.convertAndSend("/topic/temp-orders", tableAndStatus);
        // Xóa đơn hàng tạm thời khỏi Redis
        TempOrderRepo.deleteById(tableId);
        return new APIResponse<>(200, "Order closed and saved successfully", tempOrder);
    }

}
