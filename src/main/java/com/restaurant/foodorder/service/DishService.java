package com.restaurant.foodorder.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.DishReq;
import com.restaurant.foodorder.dto.DishRes;
import com.restaurant.foodorder.dto.DishSizeDTO;
import com.restaurant.foodorder.model.Dish;
import com.restaurant.foodorder.model.DishSize;
import com.restaurant.foodorder.model.DishType;
import com.restaurant.foodorder.repo.DishRepo;
import com.restaurant.foodorder.repo.DishTypeRepo;

@Service
public class DishService {
    private final DishRepo dishRepo;
    private final DishTypeRepo dishTypeRepo;

    public DishService(DishRepo dishRepo, DishTypeRepo dishTypeRepo) {
        this.dishRepo = dishRepo;
        this.dishTypeRepo = dishTypeRepo;
    }

    public APIResponse<Dish> createDish(DishReq dishReq) {
        Dish dish = new Dish();
        dish.setName(dishReq.getName());
        dish.setPrice(dishReq.getPrice());
        dish.setImage(dishReq.getImage());
        dish.setDescription(dishReq.getDescription());

        dishReq.getDishSizes().forEach(sizeReq -> {
            DishSize size = new DishSize();
            size.setName(sizeReq.getName());
            size.setAdditionalPrice(sizeReq.getAdditionalPrice());
            size.setDish(dish);
            dish.getDishSizes().add(size);
        });

        for (Long dishTypeId : dishReq.getDishTypeId()) {
            boolean exists = dishTypeRepo.existsById(dishTypeId);
            if (!exists) {
                return new APIResponse<>(400, "Dish type not found", null);
            }
        }

        List<DishType> dishTypes = dishTypeRepo.findAllById(dishReq.getDishTypeId());
        dish.setDishTypes(dishTypes);

        Dish savedDish = dishRepo.save(dish);
        return new APIResponse<>(200, "Dish created successfully", savedDish);
    }

    public APIResponse<List<DishRes>> getAllDishes() {
        List<Dish> dishes = dishRepo.findAll();
        List<DishRes> dishResponses = dishes.stream().map(dish -> {
            DishRes dishRes = new DishRes();
            dishRes.setId(dish.getId());
            dishRes.setName(dish.getName());
            dishRes.setPrice(dish.getPrice());
            dishRes.setImage(dish.getImage());
            dishRes.setDescription(dish.getDescription());
            dishRes.setDishTypeNames(dish.getDishTypes().stream().map(DishType::getName).toList());

            List<DishSizeDTO> sizeResponses = dish.getDishSizes().stream().map(size -> {
                DishSizeDTO sizeRes = new DishSizeDTO();
                sizeRes.setName(size.getName());
                sizeRes.setAdditionalPrice(size.getAdditionalPrice());
                return sizeRes;
            }).toList();

            dishRes.setDishSizes(sizeResponses);
            return dishRes;
        }).toList();

        return new APIResponse<>(200, "Dishes retrieved successfully", dishResponses);
    }

    public APIResponse<List<DishRes>> getDishesByType(Long typeId) {
        DishType dishType = dishTypeRepo.findById(typeId).orElse(null);
        if (dishType == null) {
            return new APIResponse<>(400, "Dish type not found", null);
        }
        // Nếu loại món có children thì lấy tất cả món của các loại con ngược lại chỉ
        // lấy món của loại hiện tại
        List<Dish> dishes = new ArrayList<>();
        if (dishType.getChildren().isEmpty()) {
            dishes = dishRepo.findByDishTypes_Id(typeId);
        } else {
            List<Long> typeIds = new ArrayList<>();
            typeIds.add(typeId);
            dishType.getChildren().forEach(child -> typeIds.add(child.getId()));
            dishes = dishRepo.findByDishTypes_IdIn(typeIds);
        }

        List<DishRes> dishResponses = dishes.stream().map(dish -> {
            DishRes dishRes = new DishRes();
            dishRes.setId(dish.getId());
            dishRes.setName(dish.getName());
            dishRes.setPrice(dish.getPrice());
            dishRes.setImage(dish.getImage());
            dishRes.setDescription(dish.getDescription());
            dishRes.setDishTypeNames(dish.getDishTypes().stream().map(DishType::getName).toList());

            List<DishSizeDTO> sizeResponses = dish.getDishSizes().stream().map(size -> {
                DishSizeDTO sizeRes = new DishSizeDTO();
                sizeRes.setName(size.getName());
                sizeRes.setAdditionalPrice(size.getAdditionalPrice());
                return sizeRes;
            }).toList();

            dishRes.setDishSizes(sizeResponses);
            return dishRes;
        }).toList();

        return new APIResponse<>(200, "Dishes retrieved successfully", dishResponses);
    }

}
