package com.example.ecomobile.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Integer userId;
    private Integer locationId;
}