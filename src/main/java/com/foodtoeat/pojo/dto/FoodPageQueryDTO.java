package com.foodtoeat.pojo.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

@Data
public class FoodPageQueryDTO implements Serializable{
    private int page;

    private int pageSize;

    private String name;

    private String description;

    private String category;

    private String imageUrl;
}

