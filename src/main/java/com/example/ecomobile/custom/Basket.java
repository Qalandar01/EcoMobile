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
    private Integer userId;
    private List<BasketItem> items;

    public Basket(Integer userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }
}
