package com.restaurant.foodorder.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.CartDTO;
import com.restaurant.foodorder.model.temp_redis.TempCart;
import com.restaurant.foodorder.model.temp_redis.TempCartItem;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CartService cartService;

    private final String TEST_USER_ID = "test-user";
    private final String CART_KEY = "cart:test-user";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void saveCart_WithValidCart_ShouldSaveSuccessfully() {
        // Arrange
        CartDTO cartDTO = new CartDTO();
        List<TempCartItem> items = new ArrayList<>();
        TempCartItem item = new TempCartItem();
        item.setDishId(String.valueOf(1L));
        item.setPrice(10.0);
        item.setQuantity(2);
        items.add(item);
        cartDTO.setItems(items);

        // Act
        APIResponse<TempCart> response = cartService.saveCart(TEST_USER_ID, cartDTO);

        // Assert
        verify(valueOperations).set(eq(CART_KEY), any(TempCart.class), eq(7L), eq(TimeUnit.DAYS));
        assertEquals(200, response.getStatusCode());
        assertEquals("Cart saved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(20.0, response.getData().getTotalPrice()); // 10.0 * 2
        assertEquals(1, response.getData().getItems().size());
    }

    @Test
    void saveCart_WithEmptyCart_ShouldSaveWithZeroTotal() {
        // Arrange
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(new ArrayList<>());

        // Act
        APIResponse<TempCart> response = cartService.saveCart(TEST_USER_ID, cartDTO);

        // Assert
        verify(valueOperations).set(eq(CART_KEY), any(TempCart.class), eq(7L), eq(TimeUnit.DAYS));
        assertEquals(200, response.getStatusCode());
        assertEquals(0.0, response.getData().getTotalPrice());
        assertTrue(response.getData().getItems().isEmpty());
    }

    @Test
    void addToCart_WithNewItem_ShouldAddSuccessfully() {
        // Arrange
        TempCartItem newItem = new TempCartItem();
        newItem.setDishId(String.valueOf(1L));
        newItem.setPrice(10.0);
        newItem.setQuantity(1);

        when(valueOperations.get(CART_KEY)).thenReturn(null);

        // Act
        APIResponse<TempCart> response = cartService.addToCart(TEST_USER_ID, newItem);

        // Assert
        verify(valueOperations).set(eq(CART_KEY), any(TempCart.class), eq(7L), eq(TimeUnit.DAYS));
        assertEquals(200, response.getStatusCode());
        assertEquals("Item added to cart successfully", response.getMessage());
        assertEquals(10.0, response.getData().getTotalPrice());
        assertEquals(1, response.getData().getItems().size());
    }

    @Test
    void addToCart_WithExistingItem_ShouldUpdateQuantity() {
        // Arrange
        TempCartItem existingItem = new TempCartItem();
        existingItem.setDishId(String.valueOf(1L));
        existingItem.setPrice(10.0);
        existingItem.setQuantity(1);

        TempCart existingCart = new TempCart();
        existingCart.setUserId(TEST_USER_ID);
        existingCart.getItems().add(existingItem);
        existingCart.setTotalPrice(10.0);

        when(valueOperations.get(CART_KEY)).thenReturn(existingCart);

        TempCartItem newItem = new TempCartItem();
        newItem.setDishId(String.valueOf(1L));
        newItem.setPrice(10.0);
        newItem.setQuantity(2);

        // Act
        APIResponse<TempCart> response = cartService.addToCart(TEST_USER_ID, newItem);

        // Assert
        verify(valueOperations).set(eq(CART_KEY), any(TempCart.class), eq(7L), eq(TimeUnit.DAYS));
        assertEquals(200, response.getStatusCode());
        assertEquals(1, response.getData().getItems().size());
        assertEquals(3, response.getData().getItems().get(0).getQuantity()); // 1 + 2
    }

    @Test
    void getCart_WhenCartExists_ShouldReturnCart() {
        // Arrange
        TempCart existingCart = new TempCart();
        existingCart.setUserId(TEST_USER_ID);
        when(valueOperations.get(CART_KEY)).thenReturn(existingCart);

        // Act
        APIResponse<TempCart> response = cartService.getCart(TEST_USER_ID);

        // Assert
        verify(valueOperations).get(CART_KEY);
        verify(redisTemplate).expire(CART_KEY, 7L, TimeUnit.DAYS);
        assertEquals(200, response.getStatusCode());
        assertEquals("Cart retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void getCart_WhenCartDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(valueOperations.get(CART_KEY)).thenReturn(null);

        // Act
        APIResponse<TempCart> response = cartService.getCart(TEST_USER_ID);

        // Assert
        verify(valueOperations).get(CART_KEY);
        assertEquals(404, response.getStatusCode());
        assertEquals("Cart not found", response.getMessage());
        assertNull(response.getData());
    }
}