package com.foodtoeat.controller;

import com.foodtoeat.pojo.dto.FoodPageQueryDTO;
import com.foodtoeat.pojo.vo.FoodVO;
import com.foodtoeat.result.PageResult;
import com.foodtoeat.result.Result;
import com.foodtoeat.service.FoodService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public Result<PageResult<FoodVO>> getFood(FoodPageQueryDTO foodPageQueryDTO){
        PageResult<FoodVO> pageResult = foodService.getFood(foodPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/foods/{id}")
    public Result<FoodVO> getFoodById(@PathVariable Integer id){
        return Result.success(foodService.getFoodById(id));
    }

    @GetMapping("/foods/search")
    public Result<List<FoodVO>> getFoodBySearch(@RequestParam String keyword){
        return Result.success(foodService.getFoodBySearch(keyword));
    }



}
