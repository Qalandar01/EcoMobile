package com.example.ecomobile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderWithTotalPagesDTO {
    private List<OrderDTO> orders;
    private Integer totalPages;
}
