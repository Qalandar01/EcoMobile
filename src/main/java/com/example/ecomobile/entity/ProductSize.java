package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import com.example.ecomobile.enums.Size;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class ProductSize extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private Size size;

    @ManyToMany
    private List<Product> products;
}
