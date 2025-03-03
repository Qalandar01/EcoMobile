package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Category;
import com.example.ecomobile.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategory(Category category);
}