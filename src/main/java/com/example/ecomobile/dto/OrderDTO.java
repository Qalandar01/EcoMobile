package com.example.ecomobile.dto;

import com.example.ecomobile.entity.OrderItem;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class OrderDTO {
     String status;
     Integer id;
     List<OrderItemDTO> orderItems;
     Double total;
     Integer locationId;
     LocalDate date;
}
