package com.example.ecomobile.controller;

import com.example.ecomobile.dto.ProductDTO;
import com.example.ecomobile.entity.Attachment;
import com.example.ecomobile.entity.AttachmentContent;
import com.example.ecomobile.entity.Product;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.enums.ProductBrand;
import com.example.ecomobile.repo.*;
import com.example.ecomobile.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;



    @PostMapping("/save")
    public ResponseEntity<String> saveProduct(
                                                @RequestParam String name,
                                              @RequestParam double price,
                                              @RequestParam String description,
                                              @RequestParam(required = false) Integer category,
                                              @RequestParam ProductBrand productBrand,
                                              @RequestParam List<Integer> colorIds,
                                              @RequestParam List<Integer> sizeIds,
                                              @RequestParam(required = false) MultipartFile[] attachments) {
        try {
            List<Integer> attachmentIds = new ArrayList<>();
            if (attachments != null) {
                for (MultipartFile file : attachments) {
                    Integer attachmentId = productService.uploadAttachment(file);
                    attachmentIds.add(attachmentId);
                }
            }

            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(name);
            productDTO.setPrice(price);
            productDTO.setDescription(description);
            productDTO.setCategoryId(category);
            productDTO.setProductBrand(productBrand);
            productDTO.setColorIds(colorIds);
            productDTO.setSizeIds(sizeIds);
            productDTO.setAttachmentIds(attachmentIds);

            productService.addProduct(productDTO);
            return ResponseEntity.ok("Mahsulot muvaffaqiyatli qo‘shildi!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Fayl yuklashda xatolik yuz berdi: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/{productId}/like")
    public ResponseEntity<String> likeProduct(@PathVariable Integer productId, @RequestParam Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Foydalanuvchi topilmadi!");
        }
        productService.likeProduct(productId, userId);
        return ResponseEntity.ok("Like updated");
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        Optional<ProductDTO> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/file/{attachmentId}")
    public void getFile(@PathVariable Integer attachmentId, HttpServletResponse response) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Fayl topilmadi"));

        AttachmentContent attachmentContent = attachmentContentRepository.findByAttachmentId(attachmentId);

        response.setContentType(determineContentType(attachment.getFilename()));
        response.getOutputStream().write(attachmentContent.getContent());
    }

    private String determineContentType(String filename) {
        if (filename == null) return "application/octet-stream";
        String lowerCaseFileName = filename.toLowerCase();

        if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerCaseFileName.endsWith(".png")) return "image/png";
        if (lowerCaseFileName.endsWith(".gif")) return "image/gif";
        if (lowerCaseFileName.endsWith(".webp")) return "image/webp";
        if (lowerCaseFileName.endsWith(".pdf")) return "application/pdf";

        return "application/octet-stream";
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDTO) {

        Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Integer categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }



}
