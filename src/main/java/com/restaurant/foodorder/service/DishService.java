package com.restaurant.foodorder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.DishReq;
import com.restaurant.foodorder.dto.DishRes;
import com.restaurant.foodorder.dto.DishSizeDTO;
import com.restaurant.foodorder.dto.PageResponse;
import com.restaurant.foodorder.enumm.DishStatus;
import com.restaurant.foodorder.model.Dish;
import com.restaurant.foodorder.model.DishSize;
import com.restaurant.foodorder.model.DishType;
import com.restaurant.foodorder.repo.DishRepo;
import com.restaurant.foodorder.repo.DishSpecification;
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
        dish.setDishTypes(dishTypes.stream().collect(Collectors.toSet()));

        Dish savedDish = dishRepo.save(dish);
        return new APIResponse<>(200, "Dish created successfully", savedDish);
    }

    public APIResponse<PageResponse<DishRes>> getAllDishes(List<Long> typeIds, String keyword, Double minPrice,
            Double maxPrice, DishStatus status, Pageable pageable) {

        // Nếu có loại món ăn cha thì lấy cả món ăn của loại con
        List<Long> newTypeIds = new ArrayList<>();
        if (typeIds != null) {
            typeIds.forEach(item -> {
                DishType dishType = dishTypeRepo.findById(item).orElse(null);
                if (dishType != null && dishType.getChildren() != null && !dishType.getChildren().isEmpty()) {
                    dishType.getChildren().forEach(child -> {
                        newTypeIds.add(child.getId());
                    });
                }
            });
        }
        Specification<Dish> spec = Specification
                .where(DishSpecification.hasDishTypes(newTypeIds.isEmpty() ? typeIds : newTypeIds))
                .and(DishSpecification.receiverNameContains(keyword))
                .and(DishSpecification.hasPriceBetween(minPrice, maxPrice))
                .and(DishSpecification.hasStatus(status != null ? status.name() : null));

        Page<Dish> dishes = dishRepo.findAll(spec, pageable);
        Page<DishRes> dishResponses = dishes.map(dish -> {
            DishRes dishRes = new DishRes();
            dishRes.setId(dish.getId());
            dishRes.setName(dish.getName());
            dishRes.setPrice(dish.getPrice());
            dishRes.setDiscount(dish.getDiscount());
            dishRes.setImage(dish.getImage());
            dishRes.setDescription(dish.getDescription());
            dishRes.setDishTypeNames(dish.getDishTypes().stream().map(DishType::getName).toList());
            dishRes.setStatus(dish.getStatus());
            List<DishSizeDTO> sizeResponses = dish.getDishSizes().stream().map(size -> {
                DishSizeDTO sizeRes = new DishSizeDTO();
                sizeRes.setSizeId(size.getId());
                sizeRes.setName(size.getName());
                sizeRes.setAdditionalPrice(size.getAdditionalPrice());
                return sizeRes;
            }).toList();

            dishRes.setDishSizes(sizeResponses);
            return dishRes;
        });

        return new APIResponse<>(200, "Dishes retrieved successfully", new PageResponse<>(
                dishResponses.getContent(),
                dishes.getNumber(),
                dishes.getSize(),
                dishes.getTotalElements(),
                dishes.getTotalPages(),
                dishes.isLast()));
    }

    public APIResponse<PageResponse<DishRes>> getDishesByType(Long typeId, Pageable pageable) {
        DishType dishType = dishTypeRepo.findById(typeId).orElse(null);
        if (dishType == null) {
            return new APIResponse<>(400, "Dish type not found", null);
        }

        // If the dish type has children, get dishes from all child types, otherwise get
        // dishes only from current type
        Page<Dish> dishes;
        if (dishType.getChildren().isEmpty()) {
            dishes = dishRepo.findByDishTypes_Id(typeId, pageable);
        } else {
            List<Long> typeIds = new ArrayList<>();
            typeIds.add(typeId);
            dishType.getChildren().forEach(child -> typeIds.add(child.getId()));
            dishes = dishRepo.findByDishTypes_IdIn(typeIds, pageable);
        }

        Page<DishRes> dishResponses = dishes.map(dish -> {
            DishRes dishRes = new DishRes();
            dishRes.setId(dish.getId());
            dishRes.setName(dish.getName());
            dishRes.setPrice(dish.getPrice());
            dishRes.setDiscount(dish.getDiscount());
            dishRes.setImage(dish.getImage());
            dishRes.setDescription(dish.getDescription());
            dishRes.setDishTypeNames(dish.getDishTypes().stream().map(DishType::getName).toList());
            dishRes.setStatus(dish.getStatus());
            List<DishSizeDTO> sizeResponses = dish.getDishSizes().stream().map(size -> {
                DishSizeDTO sizeRes = new DishSizeDTO();
                sizeRes.setSizeId(size.getId());
                sizeRes.setName(size.getName());
                sizeRes.setAdditionalPrice(size.getAdditionalPrice());
                return sizeRes;
            }).toList();

            dishRes.setDishSizes(sizeResponses);
            return dishRes;
        });

        return new APIResponse<>(200, "Dishes retrieved successfully",
                new PageResponse<>(
                        dishResponses.getContent(),
                        dishes.getNumber(),
                        dishes.getSize(),
                        dishes.getTotalElements(),
                        dishes.getTotalPages(),
                        dishes.isLast()));
    }

    public APIResponse<DishRes> getDishDetail(Long dishId) {
        Dish dish = dishRepo.findById(dishId).orElse(null);
        if (dish == null) {
            return new APIResponse<>(400, "Dish not found", null);
        }
        DishRes dishRes = new DishRes();
        dishRes.setId(dish.getId());
        dishRes.setName(dish.getName());
        dishRes.setPrice(dish.getPrice());
        dishRes.setImage(dish.getImage());
        dishRes.setDescription(dish.getDescription());

        List<DishSizeDTO> sizeResponses = dish.getDishSizes().stream().map(size -> {
            DishSizeDTO sizeRes = new DishSizeDTO();
            sizeRes.setSizeId(size.getId());
            sizeRes.setName(size.getName());
            sizeRes.setAdditionalPrice(size.getAdditionalPrice());
            return sizeRes;
        }).toList();

        dishRes.setDishSizes(sizeResponses);

        return new APIResponse<>(200, "Dish retrieved successfully", dishRes);
    }
}
