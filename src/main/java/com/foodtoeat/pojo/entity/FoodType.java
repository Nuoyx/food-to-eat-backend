package com.foodtoeat.pojo.entity;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
public class FoodType implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long foodId;
    private Long typeId;
}
