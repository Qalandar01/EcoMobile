package com.example.ecomobile.controller;

import com.example.ecomobile.dto.CategoryProductCountDTO;
import com.example.ecomobile.entity.Category;
import com.example.ecomobile.repo.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")

public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    @PostMapping
    public void save(@RequestBody CategoryProductCountDTO categoryProductCountDTO){
        Category category = Category
                .builder()
                .name(categoryProductCountDTO.getName())
                .build();
        categoryRepository.save(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Integer id){
        categoryRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public void update(@RequestBody CategoryProductCountDTO categoryProductCountDTO, @PathVariable Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        category.setName(categoryProductCountDTO.getName()); // Eski obyektning nomini yangilaymiz
        categoryRepository.save(category); // Yangilangan obyektni saqlaymiz
    }





}
