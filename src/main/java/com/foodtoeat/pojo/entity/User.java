package com.foodtoeat.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.security.Timestamp;

public class User implements Serializable{
    private Long id;
    private String username;
    private String password;
    private String email;
    private Timestamp createTime;
    private Timestamp updateTime;

}
