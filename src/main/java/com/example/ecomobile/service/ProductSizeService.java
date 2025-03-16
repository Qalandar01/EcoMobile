package com.example.ecomobile.service;

import com.example.ecomobile.entity.ProductSize;
import com.example.ecomobile.repo.ProductSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSizeService {
    private final ProductSizeRepository productSizeRepository;

    public List<ProductSize> getAllSizes() {
        return productSizeRepository.findAll();
    }

    public ProductSize saveSize(ProductSize productSize) {
        return productSizeRepository.save(productSize);
    }

    public String findByIdForSize(Integer sizeId) {
        return productSizeRepository.findById(sizeId).map(ProductSize::getProductSize).orElse("Size not found");
    }
}
