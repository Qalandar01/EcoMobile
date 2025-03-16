package com.example.ecomobile.service;

import com.example.ecomobile.entity.Category;
import com.example.ecomobile.repo.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServise {

    private final CategoryRepository categoryRepository;

    public CategoryServise(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    public Category findById(int id) {
       return categoryRepository.findById(id).orElseThrow();
    }
    public Category update(Integer id,Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }
    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }

    public String findByIdForName(Integer category) {
        return categoryRepository.findById(category).map(Category::getName).orElse("Noma'lum Category");
    }
}
