package com.example.ecomobile.controller;

import com.example.ecomobile.entity.ProductSize;
import com.example.ecomobile.service.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class ProductSizeController {
    private final ProductSizeService productSizeService;

    @GetMapping
    public ResponseEntity<List<ProductSize>> getAllSizes() {
        return ResponseEntity.ok(productSizeService.getAllSizes());
    }
    @PostMapping("/save")
    public ResponseEntity<ProductSize> saveSize(@RequestBody ProductSize productSize) {
        ProductSize savedSize = productSizeService.saveSize(productSize);
        return ResponseEntity.ok(savedSize);
    }
}
