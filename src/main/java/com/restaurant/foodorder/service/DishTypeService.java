package com.restaurant.foodorder.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.model.DishType;
import com.restaurant.foodorder.repo.DishTypeRepo;

@Service
public class DishTypeService {
    private final DishTypeRepo dishTypeRepo;

    public DishTypeService(DishTypeRepo dishTypeRepo) {
        this.dishTypeRepo = dishTypeRepo;
    }

    public APIResponse<List<DishType>> getAllDishTypes() {
        List<DishType> dishTypes = dishTypeRepo.findAll();
        // Xóa những dishType có parent khác null
        dishTypes.removeIf(dt -> dt.getParent() != null);
        return new APIResponse<>(200, "Lấy danh sách loại món ăn thành công", dishTypes);
    }
}