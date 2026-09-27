package com.foodtoeat.service.impl;
import com.foodtoeat.mapper.FoodMapper;
import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.entity.Food;
import com.foodtoeat.result.PageResult;
import com.foodtoeat.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


@Service
public class FoodServiceImpl implements FoodService{

    @Autowired
    private FoodMapper foodMapper;
    Random random = new Random();


    @Override
    public PageResult getFood(FoodPageQueryDTO foodPageQueryDTO) {
        Page<Food> foodsPage = foodMapper.pageQuery(foodPageQueryDTO);
        return new PageResult(foodsPage.getTotalElements(), foodsPage.getContent());
    }

    @Override
    public Food getFoodById(Integer id) {
        return foodMapper.getFoodById(id);
    }

    @Override
    public List<Food> getFoodBySearch(String keyword) {
        return foodMapper.getFoodBySearch(keyword);
    }

    @Override
    public Food getFoodByCategory(FoodCategoryFilterDTO foodCategoryFilterDTO) {
        List<Food> foods = foodMapper.getFoodByCategory(foodCategoryFilterDTO);
        int targetId = random.nextInt(1, foods.size());
        return foods.get(targetId);
    }

    @Override
    public Food random() {
        Integer total = foodMapper.count();
        Integer targetId = random.nextInt(1, total);
        return foodMapper.getFoodById(targetId);
    }
}
