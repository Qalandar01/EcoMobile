package com.example.ecomobile.custom;

import com.example.ecomobile.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasketItem {
    private Integer productId;
    private String productName;
    private Double price;
    private Integer quantity;
}
