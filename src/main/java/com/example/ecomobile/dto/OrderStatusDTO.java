package com.example.ecomobile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class OrderStatusDTO {
    Integer orderId;
    String status;
    Integer totalOrders;
    Integer deliveredOrders;
    Double totalRevenue;
}

