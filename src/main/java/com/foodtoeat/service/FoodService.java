package com.foodtoeat.service;


import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.entity.Food;
import com.foodtoeat.result.PageResult;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FoodService {

    PageResult getFood(FoodPageQueryDTO foodPageQueryDTO);

    Food getFoodById(Integer id);

    List<Food> getFoodBySearch(String keyword);

    Food getFoodByCategory(FoodCategoryFilterDTO foodCategoryFilterDTO);

    Food random();
}
