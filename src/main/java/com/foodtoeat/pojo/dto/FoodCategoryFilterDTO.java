package com.foodtoeat.pojo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FoodCategoryFilterDTO {

    private List<Integer> includeCategoryIds;

    private List<Integer> excludeCategoryIds;

    private List<Integer> rejectedFoodIds;
}