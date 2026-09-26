package com.foodtoeat.service;

import com.foodtoeat.pojo.dto.FoodDeciderRequestDTO;
import com.foodtoeat.pojo.dto.FoodRecommendationRejectDTO;
import com.foodtoeat.pojo.vo.FoodDeciderResponseVO;

import java.util.UUID;

public interface RecommendationService {
    FoodDeciderResponseVO startDecision();

    FoodDeciderResponseVO answerQuestion(FoodDeciderRequestDTO foodDeciderRequestDTO);

    FoodDeciderResponseVO rejectRecommendation(FoodRecommendationRejectDTO foodRecommendationRejectDTO);

    void acceptRecommendation(UUID sessionId);
}
