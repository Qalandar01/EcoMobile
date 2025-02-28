package com.example.ecomobile.dto;

import lombok.Value;

@Value
public class CategoryProductCountDTO {
    private Integer categoryId;
    private String categoryName;
    private Integer productCount;
}

