package com.foodtoeat.pojo.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FoodRecommendationRejectDTO {

    private UUID sessionId;

    private Long foodId;
}