package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    Integer getProductCountByCategoryId(@Param("categoryId") Integer categoryId);
}