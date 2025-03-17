package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Category;
import com.example.ecomobile.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategory(Category category);
    @Query("select count(p) from Product p where p.category.id = :categoryId ")
    Long countProductsByCategory(@Param("categoryId") Integer categoryId);
}