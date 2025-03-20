package com.example.ecomobile.dto;

import lombok.*;

@Value
@Builder
public class OrderItemDTO {
     Integer productId;
     String productName;
     Double price;
     Integer quantity;

}