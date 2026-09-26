package com.foodtoeat.service.impl;
import com.foodtoeat.mapper.FoodMapper;
import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.entity.Food;
import com.foodtoeat.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;


@Service
public class FoodServiceImpl implements FoodService{

    @Autowired
    private FoodMapper foodMapper;


    @Override
    public Food[] getFood(FoodPageQueryDTO foodPageQueryDTO) {
        return foodMapper.pageQuery(foodPageQueryDTO);
    }

    @Override
    public Food getFoodById(Integer id) {
        return foodMapper.getFoodById(id);
    }

    @Override
    public Food[] getFoodBySearch(String keyword) {
        return foodMapper.getFoodBySearch(keyword);
    }

    @Override
    public Food random() {
        int total = foodMapper.count();
        int targetId = Random.nextInt(1, total);
        return foodMapper.getFoodById(targetId);
    }
}
