package com.foodtoeat.pojo.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class FoodVO implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String description;
    private String cuisine;
    private List<String> types = new ArrayList<>();
    private List<String> ingredients = new ArrayList<>();
    private List<String> flavor = new ArrayList<>();
    @JsonProperty("image_url")
    private String imageUrl;
}
