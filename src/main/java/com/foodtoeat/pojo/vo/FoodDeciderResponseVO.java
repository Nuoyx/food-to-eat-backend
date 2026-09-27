package com.foodtoeat.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import com.foodtoeat.pojo.entity.Question;
import com.foodtoeat.pojo.entity.Food;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDeciderResponseVO {
    private UUID sessionId;
    private Question question;
    private Food recommendation;
}
