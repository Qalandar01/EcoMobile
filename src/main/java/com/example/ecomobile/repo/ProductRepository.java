package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}