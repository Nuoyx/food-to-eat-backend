package com.foodtoeat.service.impl;
import com.foodtoeat.mapper.FoodMapper;
import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.vo.FoodVO;
import com.foodtoeat.result.PageResult;
import com.foodtoeat.service.FoodService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FoodServiceImpl implements FoodService{

    private final FoodMapper foodMapper;

    public FoodServiceImpl(FoodMapper foodMapper) {
        this.foodMapper = foodMapper;
    }


    @Override
    public PageResult<FoodVO> getFood(FoodPageQueryDTO foodPageQueryDTO) {
        Page<FoodVO> foodsPage = foodMapper.pageQuery(foodPageQueryDTO);
        return new PageResult<>(foodsPage.getTotalElements(), foodsPage.getContent());
    }

    @Override
    public FoodVO getFoodById(Integer id) {
        return foodMapper.getFoodById(id);
    }

    @Override
    public List<FoodVO> getFoodBySearch(String keyword) {
        return foodMapper.getFoodBySearch(keyword);
    }

    @Override
    public FoodVO getRandomFoodByCategory(FoodCategoryFilterDTO foodCategoryFilterDTO) {
        return foodMapper.getRandomFoodByCategory(foodCategoryFilterDTO);
    }

    @Override
    public FoodVO random() {
        return foodMapper.getRandomFood();
    }
}
