package com.example.ecomobile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerDTO {
    private Integer id;
    private String name;
    private String lastMessage;
    private String time;
}
