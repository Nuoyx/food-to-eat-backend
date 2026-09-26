package com.foodtoeat.controller;


import com.foodtoeat.pojo.dto.FoodDeciderRequestDTO;
import com.foodtoeat.pojo.dto.FoodRecommendationRejectDTO;
import com.foodtoeat.pojo.entity.Food;
import com.foodtoeat.pojo.vo.FoodDeciderResponseVO;
import com.foodtoeat.result.Result;
import com.foodtoeat.service.FoodService;
import com.foodtoeat.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final FoodService foodService;

    public RecommendationController(
            RecommendationService recommendationService,
            FoodService foodService
    ) {
        this.recommendationService = recommendationService;
        this.foodService = foodService;
    }

    @PostMapping("/decide/start")
    public Result<FoodDeciderResponseVO> startDecision() {

        log.info("Starting new food decision session");

        FoodDeciderResponseVO response = recommendationService.startDecision();

        return Result.success(response);
    }

    @PostMapping("/decide/answer")
    public Result<FoodDeciderResponseVO> answerQuestion(
            @RequestBody FoodDeciderRequestDTO foodDeciderRequestDTO) {
        return Result.success(
                recommendationService.answerQuestion(foodDeciderRequestDTO)
        );
    }

    @PostMapping("/decide/reject")
    public Result<FoodDeciderResponseVO> rejectRecommendation(
            @RequestBody FoodRecommendationRejectDTO foodRecommendationRejectDTO) {
        return Result.success(
                recommendationService.rejectRecommendation(foodRecommendationRejectDTO)
        );
    }

    @PostMapping("/decide/accept")
    public void acceptRecommendation(
            @RequestBody UUID sessionId) {
        recommendationService.acceptRecommendation(sessionId);
    }

    @GetMapping("/random")
    public Result<Food> getRandom(){
        return Result.success(foodService.random());
    }

}
