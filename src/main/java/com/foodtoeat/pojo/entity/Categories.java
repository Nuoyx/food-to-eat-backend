package com.foodtoeat.pojo.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class Categories implements Serializable{
    private Long id;
    private String name;
    // Exactly one target is set: the attribute this decision category represents.
    private Long cuisineId;
    private Long typeId;
    private Long ingredientId;
    private Long flavorId;
}
