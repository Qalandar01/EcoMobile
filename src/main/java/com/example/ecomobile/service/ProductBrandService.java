package com.example.ecomobile.service;

import com.example.ecomobile.entity.ProductBrand;
import com.example.ecomobile.repo.ProductBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductBrandService {

    private final ProductBrandRepository productBrandRepository;

    public List<ProductBrand> getAllBrands() {
        return productBrandRepository.findAll();
    }

    public ProductBrand saveBrand(ProductBrand productBrand) {
        return productBrandRepository.save(productBrand);
    }

    public String findByIdForName(Integer productBrandId) {
        return productBrandRepository.findById(productBrandId)
                .map(ProductBrand::getProductBrand)
                .orElse("Noma'lum brend");
    }

}
