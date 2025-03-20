package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductBrand extends BaseEntity {
    private String productBrand;
}