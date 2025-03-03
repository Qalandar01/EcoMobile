package com.example.ecomobile.dto;

import com.example.ecomobile.enums.ProductBrand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    Integer id;
    String name;
    Double price;
    String description;
    ProductBrand productBrand;
    List<Integer> attachmentIds;
    Integer categoryId;
    List<Integer> colorIds;
    List<Integer> sizeIds;
    private List<Integer> likedByUsers;
}