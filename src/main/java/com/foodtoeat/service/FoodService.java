package com.foodtoeat.service;


import com.foodtoeat.pojo.dto.FoodCategoryFilterDTO;
import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.vo.FoodVO;
import com.foodtoeat.result.PageResult;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FoodService {

    PageResult<FoodVO> getFood(FoodPageQueryDTO foodPageQueryDTO);

    FoodVO getFoodById(Integer id);

    List<FoodVO> getFoodBySearch(String keyword);

    FoodVO getRandomFoodByCategory(FoodCategoryFilterDTO foodCategoryFilterDTO);

    FoodVO random();
}
