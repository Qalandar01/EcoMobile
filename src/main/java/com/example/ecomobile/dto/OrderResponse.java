package com.example.ecomobile.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Integer id;
    private LocalDateTime createdDate;
    private String status;
    private String location;
    private List<OrderItemResponse> items;
    private Double totalAmount; // Umumiy summa
}