package com.example.ecomobile.repo;

import com.example.ecomobile.entity.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductBrandRepository extends JpaRepository<ProductBrand, Integer> {
  Optional<ProductBrand> findByProductBrand(String productBrand);
}