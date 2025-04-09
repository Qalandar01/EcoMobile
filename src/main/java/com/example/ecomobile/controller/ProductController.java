package com.example.ecomobile.controller;

import com.example.ecomobile.dto.ProductDTO;
import com.example.ecomobile.dto.ProductVariantDTO;
import com.example.ecomobile.entity.Attachment;
import com.example.ecomobile.entity.AttachmentContent;
import com.example.ecomobile.entity.Product;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.repo.AttachmentContentRepository;
import com.example.ecomobile.repo.AttachmentRepository;
import com.example.ecomobile.repo.CategoryRepository;
import com.example.ecomobile.repo.ProductRepository;
import com.example.ecomobile.repo.UserRepository;
import com.example.ecomobile.service.*;
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
    private final UserRepository userRepository;
    private final ProductBrandService productBrandService;
    private final CategoryServise categoryServise;
    private final ProductColorService productColorService;
    private final ProductSizeService productSizeService;

    @PostMapping("/save")
    public ResponseEntity<String> saveProduct(
            @RequestParam String productName,
            @RequestParam double price,
            @RequestParam String description,
            @RequestParam(required = false) Optional<Integer> category,
            @RequestParam Integer productBrandId,
            @RequestParam Integer colorId,
            @RequestParam Integer sizeId,
            @RequestParam Integer amount,
            @RequestParam Integer userId,
            @RequestParam(required = false) MultipartFile[] attachments) {
        try {
            if (productName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mahsulot nomi bo'sh bo'lmasligi kerak!");
            }

            // Fayllarni yuklash
            List<Integer> attachmentIds = new ArrayList<>();
            if (attachments != null) {
                for (MultipartFile file : attachments) {
                    Integer attachmentId = productService.uploadAttachment(file);
                    attachmentIds.add(attachmentId);
                }
            }

            System.out.println("Bu category==============="+category);

            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price(price)
                    .description(description)
                    .categoryId(category.orElse(null))
                    .productBrandId(productBrandId)
                    .colorId(colorId)
                    .sizeId(sizeId)
                    .attachmentIds(attachmentIds)
                    .amount(amount)
                    .userId(userId)
                    .productBrand(productBrandService.findByIdForName(productBrandId))
                    .categoryName(category.map(categoryServise::findByIdForName).orElse(null))
                    .productColor(productColorService.findByIdForName(colorId))
                    .productSize(productSizeService.findByIdForSize(sizeId))
                    .build();

            // Mahsulotni saqlash
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
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
//        Optional<ProductDTO> product = productService.getProductById(id);
//        return product.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        Optional<ProductDTO> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/variants")
    public ResponseEntity<?> getProductVariants(@RequestParam String name) {
        List<ProductVariantDTO> variants = productService.getProductVariants(name);
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/variant")
    public ResponseEntity<?> getProductVariant(
            @RequestParam String name,
            @RequestParam String color,
            @RequestParam String size) {
        Optional<ProductDTO> variant = productService.getProductVariant(name, color, size);
        return variant.map(ResponseEntity::ok)
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

        if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg"))
            return "image/jpeg";
        if (lowerCaseFileName.endsWith(".png"))
            return "image/png";
        if (lowerCaseFileName.endsWith(".gif"))
            return "image/gif";
        if (lowerCaseFileName.endsWith(".webp"))
            return "image/webp";
        if (lowerCaseFileName.endsWith(".pdf"))
            return "application/pdf";

        return "application/octet-stream";
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Mahsulotni yangilashda xatolik yuz berdi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Mahsulot muvaffaqiyatli o'chirildi");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Mahsulotni o'chirishda xatolik yuz berdi: " + e.getMessage());
        }
    }



    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Integer categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}/average-rating")
    public Double getAverageRating(@PathVariable Integer productId) {
        return productService.getAverageRating(productId);
    }

    @GetMapping("/favourite-products/{userId}")
    public ResponseEntity<List<ProductDTO>> getFavouriteProducts(@PathVariable Integer userId) {
        List<ProductDTO> favouriteProducts = productService.getFavouriteProducts(userId);
        return ResponseEntity.ok(favouriteProducts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>
            >
    searchProducts(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        List<ProductDTO> searchResults = productService.searchProducts(query, page, size);
        return ResponseEntity.ok(searchResults);
    }
}
