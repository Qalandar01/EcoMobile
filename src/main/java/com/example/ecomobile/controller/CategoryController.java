package com.example.ecomobile.controller;

import com.example.ecomobile.dto.CategoryProductCountDTO;
import com.example.ecomobile.entity.Category;
import com.example.ecomobile.repo.CategoryRepository;
import com.example.ecomobile.service.CategoryServise;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")

public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryServise categoryServise;

    public CategoryController(CategoryRepository categoryRepository, CategoryServise categoryServise) {
        this.categoryRepository = categoryRepository;
        this.categoryServise = categoryServise;
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

        category.setName(categoryProductCountDTO.getName());
        categoryRepository.save(category);
    }

    @GetMapping("/product-count")
    public List<CategoryProductCountDTO> getCategoriesWithProductCount() {
        return categoryServise.getCategoriesWithProductCount();
    }



}
