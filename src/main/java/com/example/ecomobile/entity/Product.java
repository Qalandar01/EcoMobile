package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import com.example.ecomobile.enums.ColorName;
import com.example.ecomobile.enums.ProductBrand;
import com.example.ecomobile.enums.Size;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product extends BaseEntity {
    private String name;
    private Double price;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProductBrand productBrand;

    @OneToMany
    private List<Attachment> attachment;

    @ManyToOne
    private Category category;
}
