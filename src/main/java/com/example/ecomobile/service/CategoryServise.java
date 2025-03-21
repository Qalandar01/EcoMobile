package com.example.ecomobile.service;

import com.example.ecomobile.dto.CategoryProductCountDTO;
import com.example.ecomobile.entity.Category;
import com.example.ecomobile.repo.CategoryRepository;
import com.example.ecomobile.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServise {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServise(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
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

    public List<CategoryProductCountDTO> getCategoriesWithProductCount() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> new CategoryProductCountDTO(
                        category.getId(),
                        category.getName(),
                        productRepository.countProductsByCategory(category.getId())
                ))
                .collect(Collectors.toList());
    }

}
