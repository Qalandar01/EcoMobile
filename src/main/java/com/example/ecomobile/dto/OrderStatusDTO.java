package com.example.ecomobile.dto;

import lombok.Value;

@Value
public class OrderStatusDTO {
    Integer orderId;
    String status;
}
