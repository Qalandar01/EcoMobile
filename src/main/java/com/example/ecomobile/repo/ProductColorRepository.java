package com.example.ecomobile.repo;

import com.example.ecomobile.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductColorRepository extends JpaRepository<ProductColor, Integer> {
    Optional<ProductColor> findByProductColor(String productColor);
}