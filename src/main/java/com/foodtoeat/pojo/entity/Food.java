package com.foodtoeat.pojo.entity;

import lombok.Data;

import java.io.Serializable;

public class Food implements Serializable{
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
}
