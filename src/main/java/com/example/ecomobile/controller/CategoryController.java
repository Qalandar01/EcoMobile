package com.example.ecomobile.controller;

import com.example.ecomobile.dto.CategoryProductCountDTO;
import com.example.ecomobile.entity.Category;
import com.example.ecomobile.repo.CategoryRepository;
import com.example.ecomobile.repo.ProductRepository;
import com.example.ecomobile.service.CategoryServise;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServise categoryService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @PostMapping("/category")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.create(category));
    }

    @GetMapping("/category")
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/categoryWithProQuantity")
    public ResponseEntity<?> getCategoryWithProQuantity() {
        List<Category> all = categoryService.findAll();
       List<CategoryProductCountDTO> categoryProductCountDTOS = new ArrayList<>();
        for (Category category : all) {
           categoryProductCountDTOS.add(new CategoryProductCountDTO(category.getId(),category.getName(),productRepository.getProductCountByCategoryId(category.getId())));
        }
        return ResponseEntity.ok(categoryProductCountDTOS);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Category> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<Category> update(@PathVariable Integer id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.update(id, category));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
