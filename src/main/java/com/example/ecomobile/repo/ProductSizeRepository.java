package com.example.ecomobile.repo;

import com.example.ecomobile.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer> {
    Optional<ProductSize> findByProductSize(String productSize);
}