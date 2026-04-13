package com.project.test.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private Integer id;
    private String username;
    private String password;
    private String authority;
    private Integer level;
}
