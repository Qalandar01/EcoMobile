package com.example.ecomobile.repo;

import com.example.ecomobile.entity.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductBrandRepository extends JpaRepository<ProductBrand, Integer> {
  }