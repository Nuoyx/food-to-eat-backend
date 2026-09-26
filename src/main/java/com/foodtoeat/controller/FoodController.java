package com.foodtoeat.controller;

import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.entity.Food;
import com.foodtoeat.result.PageResult;
import com.foodtoeat.result.Result;
import com.foodtoeat.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    public Result<PageResult> getFood(FoodPageQueryDTO foodPageQueryDTO){
        PageResult pageResult = foodService.getFood(foodPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/foods/{id}")
    public Result<Food> getFoodById(@PathVariable Integer id){
        return Result.success(foodService.getFoodById(id));
    }

    @GetMapping("/foods/search")
    public Result<Food[]> getFoodBySearch(@RequestParam String keyword){
        return Result.success(foodService.getFoodBySearch(keyword));
    }



}
