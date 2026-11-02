package com.foodtoeat.pojo.entity;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
public class FoodIngredient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long foodId;
    private Long ingredientId;
}
