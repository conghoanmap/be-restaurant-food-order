package com.restaurant.foodorder.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.FoodDetail;
import com.restaurant.foodorder.dto.FoodReq;
import com.restaurant.foodorder.dto.FoodRes;
import com.restaurant.foodorder.model.Food;
import com.restaurant.foodorder.model.FoodType;
import com.restaurant.foodorder.repo.FoodRepo;
import com.restaurant.foodorder.repo.FoodTypeRepo;

@Service
public class FoodService {
    private final FoodRepo foodRepository;
    private final FoodTypeRepo foodTypeRepository;

    public FoodService(FoodRepo foodRepository, FoodTypeRepo foodTypeRepository) {
        this.foodRepository = foodRepository;
        this.foodTypeRepository = foodTypeRepository;
    }

    public APIResponse<List<FoodRes>> getAllFoods() {
        List<FoodRes> foods = foodRepository.findAll().stream()
                .map(food -> new FoodRes(food.getId(), food.getName(), food.getPrice(), food.getImage(),
                        food.getDescription(), food.isAvailable()))
                .collect(Collectors.toList());
        return new APIResponse<>(200, "Fetched all foods", foods);
    }

    public APIResponse<FoodDetail> getFoodById(long id) {
        return foodRepository.findById(id)
                .map(food -> {
                    FoodDetail foodDetail = new FoodDetail(food.getId(), food.getName(), food.getPrice(),
                            food.getImage(), food.getDescription(), food.isAvailable(),
                            food.getFoodType() != null ? food.getFoodType().getName() : null);
                    return new APIResponse<>(200, "Food found", foodDetail);
                })
                .orElse(new APIResponse<>(404, "Food not found", null));
    }

    public APIResponse<FoodRes> createFood(FoodReq foodReq) {
        Food food = new Food();
        food.setName(foodReq.getName());
        food.setPrice(foodReq.getPrice());
        food.setImage(foodReq.getImage());
        food.setDescription(foodReq.getDescription());
        food.setAvailable(foodReq.isAvailable());
        FoodType foodType = foodTypeRepository.findById(foodReq.getFoodTypeId()).orElse(null);
        food.setFoodType(foodType);
        Food savedFood = foodRepository.save(food);
        return new APIResponse<>(201, "Food created",
                new FoodRes(savedFood.getId(), savedFood.getName(), savedFood.getPrice(), savedFood.getImage(),
                        savedFood.getDescription(), savedFood.isAvailable()));
    }

    public APIResponse<Food> updateFood(long id, FoodReq foodReq) {
        return foodRepository.findById(id)
                .map(food -> {
                    food.setName(foodReq.getName());
                    food.setPrice(foodReq.getPrice());
                    food.setImage(foodReq.getImage());
                    food.setDescription(foodReq.getDescription());
                    food.setAvailable(foodReq.isAvailable());
                    FoodType foodType = foodTypeRepository.findById(foodReq.getFoodTypeId()).orElse(null);
                    food.setFoodType(foodType);
                    Food updatedFood = foodRepository.save(food);
                    return new APIResponse<>(200, "Food updated", updatedFood);
                })
                .orElse(new APIResponse<>(404, "Food not found", null));
    }

    public APIResponse<Void> deleteFood(long id) {
        return foodRepository.findById(id)
                .map(food -> {
                    foodRepository.delete(food);
                    return new APIResponse<Void>(200, "Food deleted", null);
                })
                .orElse(new APIResponse<>(404, "Food not found", null));
    }
}
