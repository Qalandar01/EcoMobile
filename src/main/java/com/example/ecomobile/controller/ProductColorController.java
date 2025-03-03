package com.example.ecomobile.controller;

import com.example.ecomobile.entity.ProductColor;
import com.example.ecomobile.service.ProductColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ProductColorController {
    private final ProductColorService productColorService;

    @GetMapping
    public ResponseEntity<List<ProductColor>> getAllColors() {
        return ResponseEntity.ok(productColorService.getAllColors());
    }
}
