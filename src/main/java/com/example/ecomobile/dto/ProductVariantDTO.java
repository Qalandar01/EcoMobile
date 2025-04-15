package com.example.ecomobile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {
    private String name;
    private List<String> available_sizes;
    private List<String> available_colors;
    private List<Integer> attachment_ids;
}

