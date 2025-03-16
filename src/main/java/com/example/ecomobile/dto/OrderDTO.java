package com.example.ecomobile.dto;

import com.example.ecomobile.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String status;
    private Integer id;
    private List<OrderItem> orderItems;
    private Double total;
    private String location;
}
