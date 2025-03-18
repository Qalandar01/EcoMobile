package com.example.ecomobile.service;

import com.example.ecomobile.entity.ProductColor;
import com.example.ecomobile.repo.ProductColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductColorService {
    private final ProductColorRepository productColorRepository;

    public List<ProductColor> getAllColors() {
        return productColorRepository.findAll();
    }

    public ProductColor saveColor(ProductColor productColor) {
        Optional<ProductColor> existingColor = productColorRepository.findByProductColor(productColor.getProductColor());
        if(existingColor.isPresent()){
            return existingColor.get();
        }
        return productColorRepository.save(productColor);
    }

    public String findByIdForName(Integer colorId) {
        return productColorRepository.findById(colorId).map(ProductColor::getProductColor).orElse("Color not found");
    }
}
