package com.project.test.entity;

import lombok.Data;

@Data
public class InterfaceLevel {
    private Long id;
    private String interfacePath;
    private String interfaceMethod;
    private String allowedLevels;
    private String description;
    private Integer status;
}
