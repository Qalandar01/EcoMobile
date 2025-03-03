package com.example.ecomobile.service;

import com.example.ecomobile.entity.ProductColor;
import com.example.ecomobile.repo.ProductColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductColorService {
    private final ProductColorRepository productColorRepository;

    public List<ProductColor> getAllColors() {
        return productColorRepository.findAll();
    }
}
