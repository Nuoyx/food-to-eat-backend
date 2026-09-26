package com.foodtoeat.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

public class Recommendation implements Serializable {
    private Long id;
    private Long userId;
    private Long questionId;
    private Long foodId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
