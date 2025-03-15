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
    public ResponseEntity<ProductBrand> saveBrand(@RequestBody ProductBrand productBrand) {
        ProductBrand savedBrand = productBrandService.saveBrand(productBrand);
        return ResponseEntity.ok(savedBrand);
    }
}
