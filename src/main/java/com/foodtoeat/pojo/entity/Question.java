package com.foodtoeat.pojo.entity;

import java.io.Serializable;
import lombok.Data;

public class Question implements Serializable{
    private Long id;
    private Long categoryId;
    private String question;
}
