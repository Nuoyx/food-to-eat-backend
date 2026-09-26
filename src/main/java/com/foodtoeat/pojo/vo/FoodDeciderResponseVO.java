package com.foodtoeat.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import com.foodtoeat.pojo.entity.Question;
import com.foodtoeat.pojo.entity.Food;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDeciderResponseVO {
    private Question question;
    private Food food;
}
