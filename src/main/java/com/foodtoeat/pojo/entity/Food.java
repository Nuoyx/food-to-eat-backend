package com.foodtoeat.pojo.entity;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/** One row in foods. Related names and collections belong in FoodVO. */
@Data
public class Food implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String description;
    private Long cuisineId;
    private String imageUrl;
}
