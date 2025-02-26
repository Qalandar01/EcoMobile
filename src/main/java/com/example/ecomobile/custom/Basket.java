package com.example.ecomobile.custom;

import com.example.ecomobile.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Basket {
    private List<BasketItem> basketItems = new ArrayList<>();
    private Double totalSum;
    private Integer userId;

    public Double getTotalSum() {
        Double sum = 0.0;
        for (BasketItem item : basketItems) {
            sum += item.getProduct().getPrice() * item.getQuantity();
        }
        return sum;
    }
}
