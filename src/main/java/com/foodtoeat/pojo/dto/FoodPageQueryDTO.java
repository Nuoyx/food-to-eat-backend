package com.foodtoeat.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FoodPageQueryDTO implements Serializable{
    private int page = 1;

    private int pageSize = 20;

    // Convert the one-based page number to the number of rows SQL should skip.
    public long getOffset() {
        return ((long) page - 1) * pageSize;
    }

    private String name;

    private String description;

    private String cuisine;
    private List<String> types;
    private List<String> ingredients;
    private List<String> flavor;

    private String imageUrl;
}

