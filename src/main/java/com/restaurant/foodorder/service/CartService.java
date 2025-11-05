package com.restaurant.foodorder.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.CartDTO;
import com.restaurant.foodorder.model.temp_redis.TempCart;
import com.restaurant.foodorder.model.temp_redis.TempCartItem;

@Service
@Slf4j
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String PREFIX = "cart:"; // key trong redis sẽ là cart:userId
    private final long TTL_DAYS = 7; // giỏ hàng hết hạn sau 7 ngày

    public CartService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public APIResponse<TempCart> saveCart(String userId, CartDTO cartDTO) {
        // Tính tiền tổng
        double totalPrice = 0;
        if (!cartDTO.getItems().isEmpty()) {
            totalPrice = cartDTO.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
        }

        TempCart tempCart = new TempCart();
        tempCart.setUserId(userId);
        tempCart.setItems(cartDTO.getItems());
        tempCart.setTotalPrice(totalPrice);

        redisTemplate.opsForValue().set(PREFIX + userId, tempCart, TTL_DAYS, TimeUnit.DAYS);
        return new APIResponse<>(200, "Cart saved successfully", tempCart);
    }

    public APIResponse<TempCart> addToCart(String userId, TempCartItem tempCartItem) {
        String key = PREFIX + userId;

        // Lấy giỏ hàng hiện tại từ Redis
        TempCart existingCart = (TempCart) redisTemplate.opsForValue().get(key);
        if (existingCart == null) {
            // Nếu chưa có giỏ hàng, tạo mới
            existingCart = new TempCart();
            existingCart.setUserId(userId);
            existingCart.getItems().add(tempCartItem);
            existingCart.setTotalPrice(tempCartItem.getPrice() * tempCartItem.getQuantity());
        } else {
            // Nếu đã có giỏ hàng, cập nhật
            // Kiểm tra nếu đã tồn tại sản phẩm thì cộng thêm số lượng, nếu chưa thì thêm
            // mới
            Optional<TempCartItem> existingItem = existingCart.getItems().stream()
                    .filter(item -> tempCartItem.getDishId().equals(item.getDishId()))
                    .findFirst();

            if (existingItem.isPresent()) {
                TempCartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + tempCartItem.getQuantity());
            } else {
                existingCart.setTotalPrice(existingCart.getTotalPrice() + tempCartItem.getPrice());
                existingCart.getItems().add(tempCartItem);
            }
            log.info("Adding new item to cart: {}", existingCart);
        }
        redisTemplate.opsForValue().set(key, existingCart, TTL_DAYS, TimeUnit.DAYS);
        return new APIResponse<>(200, "Item added to cart successfully", existingCart);
    }

    public APIResponse<TempCart> getCart(String userId) {
        String key = PREFIX + userId;
        TempCart existingCart = (TempCart) redisTemplate.opsForValue().get(key);
        if (existingCart == null) {
            return new APIResponse<>(404, "Cart not found", null);
        } else {
            // Cập nhật lại TTL mỗi khi truy xuất giỏ hàng
            redisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);
        }
        return new APIResponse<>(200, "Cart retrieved successfully", existingCart);
    }
}
