package com.example.ecomobile.controller;

import com.example.ecomobile.entity.ProductColor;
import com.example.ecomobile.service.ProductColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ProductColorController {
    private final ProductColorService productColorService;


    @GetMapping
    public ResponseEntity<List<ProductColor>> getAllColors() {
        List<ProductColor> colors = productColorService.getAllColors();
        return ResponseEntity.ok(colors);
    }

    @PostMapping("/save")
    public ResponseEntity<ProductColor> saveColor(@RequestBody ProductColor productColor) {
        try {
            ProductColor savedColor = productColorService.saveColor(productColor);
            return ResponseEntity.ok(savedColor);
        } catch (Exception e) {
            System.err.println("Xatolik yuz berdi: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
