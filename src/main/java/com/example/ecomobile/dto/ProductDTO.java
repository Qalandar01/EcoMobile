package com.example.ecomobile.dto;

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
    private Integer id;
    private String name;
    private Double price;
    private String description;
    private Integer productBrandId;
    private List<Integer> attachmentIds;
    private Integer categoryId;
    private Integer colorId;
    private Integer sizeId;
    private List<Integer> likedByUsers;
    private Integer amount;

    private String productBrandName;
    private String categoryName;
    private String colorName;
    private String sizeName;
}
