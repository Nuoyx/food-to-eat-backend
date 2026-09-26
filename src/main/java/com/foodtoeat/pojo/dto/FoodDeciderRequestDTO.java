package com.foodtoeat.pojo.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FoodDeciderRequestDTO {

    private UUID sessionId;

    private Boolean answer;
}