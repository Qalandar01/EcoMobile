package com.example.ecomobile.controller;

import com.example.ecomobile.entity.ProductBrand;
import com.example.ecomobile.service.ProductBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class ProductBrandController {

    private final ProductBrandService productBrandService;

    @GetMapping
    public ResponseEntity<List<ProductBrand>> getAllBrands() {
        List<ProductBrand> brands = productBrandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBrand(@RequestBody ProductBrand productBrand) {
        if (productBrand == null ||
                productBrand.getProductBrand() == null ||
                productBrand.getProductBrand().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Product brand maydoni bo‘sh bo‘lmasligi kerak.");
        }
        ProductBrand savedBrand = productBrandService.saveBrand(productBrand);
        return ResponseEntity.ok(savedBrand);
    }

}

