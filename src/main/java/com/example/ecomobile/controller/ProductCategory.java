package com.example.ecomobile.controller;

import com.example.ecomobile.entity.Attachment;
import com.example.ecomobile.entity.Product;
import com.example.ecomobile.enums.ProductBrand;
import com.example.ecomobile.repo.AttachmentRepository;
import com.example.ecomobile.repo.CategoryRepository;
import com.example.ecomobile.repo.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductCategory {

    private final ProductRepository productRepository;
    private final UploadController uploadController;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;

    public ProductCategory(ProductRepository productRepository, UploadController uploadController, CategoryRepository categoryRepository, AttachmentRepository attachmentRepository) {
        this.productRepository = productRepository;
        this.uploadController = uploadController;
        this.categoryRepository = categoryRepository;
        this.attachmentRepository = attachmentRepository;
    }
    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return ResponseEntity.ok(productRepository.findById(id).orElseThrow());
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(productRepository.findAll());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
      productRepository.deleteById(id);
      return ResponseEntity.ok(201);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update( @PathVariable("id") Integer id,
                                     @RequestParam("name") String name,
                                     @RequestParam("price") Double price,
                                     @RequestParam("categoryId")Integer categoryId,
                                     @RequestParam("images") List<MultipartFile> image,
                                     @RequestParam("description") String description,
                                     @RequestParam("productBrand")ProductBrand productBrand) {
        try {
            List<Attachment> attachments=new ArrayList<>();
            for (MultipartFile multipartFile : image) {
                attachments.add(uploadController.uploadFile(multipartFile));
            }
            Product product = new Product();
            product.setName(name);
            product.setId(id);
            product.setPrice(price);
            product.setCategory(categoryRepository.findById(categoryId).orElseThrow());
            product.setAttachment(attachments);
            product.setDescription(description);
            product.setProductBrand(productBrand);
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving product: " + e.getMessage());
        }
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("categoryId")Integer categoryId,
            @RequestParam("images") List<MultipartFile> image,
            @RequestParam("description") String description,
            @RequestParam("productBrand")ProductBrand productBrand
            ) {

        try {
            List<Attachment> attachments=new ArrayList<>();
            for (MultipartFile multipartFile : image) {
                attachments.add(uploadController.uploadFile(multipartFile));
            }
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setCategory(categoryRepository.findById(categoryId).orElseThrow());
            product.setAttachment(attachments);
            product.setDescription(description);
            product.setProductBrand(productBrand);
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving product: " + e.getMessage());
        }
    }

}
