package com.project.test.entity;

import lombok.Data;

@Data
public class UserEntity {
    private int id;
    private String username;
    private Integer level;
    private String password;
    private int deleteFlag;
}
