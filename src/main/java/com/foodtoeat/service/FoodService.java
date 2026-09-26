package com.foodtoeat.service;


import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.entity.Food;

public interface FoodService {

    Food[] getFood(FoodPageQueryDTO foodPageQueryDTO);

    Food getFoodById(Integer id);

    Food[] getFoodBySearch(String keyword);
}
