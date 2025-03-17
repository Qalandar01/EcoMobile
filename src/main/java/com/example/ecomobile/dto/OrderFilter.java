package com.example.ecomobile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilter {
    private String search;
    private LocalDate date;
    private Integer page = 1;
    private Integer size = 10;
}
