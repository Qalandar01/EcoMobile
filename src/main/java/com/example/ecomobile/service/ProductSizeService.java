package com.example.ecomobile.service;

import com.example.ecomobile.entity.ProductSize;
import com.example.ecomobile.repo.ProductSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSizeService {
    private final ProductSizeRepository productSizeRepository;

    public List<ProductSize> getAllSizes() {
        return productSizeRepository.findAll();
    }

    public ProductSize saveSize(ProductSize productSize) {
        Optional<ProductSize> existingSize = productSizeRepository.findByProductSize(productSize.getProductSize());
        if(existingSize.isPresent()){
            return existingSize.get();
        }
        return productSizeRepository.save(productSize);
    }

    public String findByIdForSize(Integer sizeId) {
        return productSizeRepository.findById(sizeId).map(ProductSize::getProductSize).orElse("Size not found");
    }
}
