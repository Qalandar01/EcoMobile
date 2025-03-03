package com.example.ecomobile.repo;

import com.example.ecomobile.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductColorRepository extends JpaRepository<ProductColor, Integer> {
}