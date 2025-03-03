package com.example.ecomobile.service;

import com.example.ecomobile.dto.ProductDTO;
import com.example.ecomobile.entity.*;
import com.example.ecomobile.enums.ProductBrand;
import com.example.ecomobile.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductSizeRepository productSizeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Integer uploadAttachment(MultipartFile file) throws IOException {
        Attachment attachment = Attachment.builder()
                .filename(file.getOriginalFilename())
                .build();
        attachmentRepository.save(attachment);

        AttachmentContent attachmentContent = AttachmentContent.builder()
                .attachment(attachment)
                .content(file.getBytes())
                .build();
        attachmentContentRepository.save(attachmentContent);

        return attachment.getId();
    }

    public void addProduct(ProductDTO productDTO) {
        if (productDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("Kategoriya tanlanishi shart!");
        }

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategoriya ID noto‘g‘ri yoki topilmadi!"));

        List<ProductColor> colors = (productDTO.getColorIds() != null && !productDTO.getColorIds().isEmpty())
                ? productColorRepository.findAllById(productDTO.getColorIds())
                : new ArrayList<>();

        List<ProductSize> sizes = (productDTO.getSizeIds() != null && !productDTO.getSizeIds().isEmpty())
                ? productSizeRepository.findAllById(productDTO.getSizeIds())
                : new ArrayList<>();

        List<Attachment> attachments = (productDTO.getAttachmentIds() != null && !productDTO.getAttachmentIds().isEmpty())
                ? attachmentRepository.findAllById(productDTO.getAttachmentIds())
                : new ArrayList<>();

        ProductBrand productBrand;
        try {
            productBrand = productDTO.getProductBrand() != null ?
                    ProductBrand.valueOf(productDTO.getProductBrand().name()) : null;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Noto‘g‘ri brend tanlandi: " + productDTO.getProductBrand());
        }

        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .productBrand(productBrand)
                .attachment(attachments)
                .colors(colors)
                .sizes(sizes)
                .category(category)
                .build();

        productRepository.save(product);
    }

    public Product updateProduct(Integer id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mahsulot topilmadi"));

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategoriya topilmadi"));

        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setProductBrand(productDTO.getProductBrand());
        product.setCategory(category);

        if (productDTO.getAttachmentIds() != null) {
            List<Attachment> attachments = attachmentRepository.findAllById(productDTO.getAttachmentIds());
            product.setAttachment(attachments);
        }

        if (productDTO.getColorIds() != null) {
            List<ProductColor> colors = productColorRepository.findAllById(productDTO.getColorIds());
            product.setColors(colors);
        }

        if (productDTO.getSizeIds() != null) {
            List<ProductSize> sizes = productSizeRepository.findAllById(productDTO.getSizeIds());
            product.setSizes(sizes);
        }

        return productRepository.save(product);
    }



    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Attachment> attachments = product.getAttachment();
        attachmentRepository.deleteAll(attachments);

        product.getColors().clear();
        product.getSizes().clear();
        product.getLikedByUsers().clear();
        productRepository.save(product);
        productRepository.delete(product);
        productRepository.deleteById(id);
    }
    // ProductService.java, getAllProducts metodi
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> ProductDTO.builder()
                        .id(product.getId())  // ID ni shu yerda qoʻshyapmiz
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .productBrand(product.getProductBrand())
                        .categoryId(product.getCategory().getId())
                        .attachmentIds(product.getAttachment().stream()
                                .map(Attachment::getId)
                                .collect(Collectors.toList()))
                        .colorIds(product.getColors().stream()
                                .map(ProductColor::getId)
                                .collect(Collectors.toList()))
                        .sizeIds(product.getSizes().stream()
                                .map(ProductSize::getId)
                                .collect(Collectors.toList()))
                        .likedByUsers(product.getLikedByUsers().stream()
                                .map(User::getId)
                                .collect(Collectors.toList()))
                        .build()
                )
                .collect(Collectors.toList());
    }





    public Optional<ProductDTO> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(product -> ProductDTO.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .productBrand(product.getProductBrand())
                        .categoryId(product.getCategory().getId())
                        .attachmentIds(product.getAttachment().stream()
                                .map(Attachment::getId)
                                .collect(Collectors.toList()))
                        .build());
    }

    public void likeProduct(Integer productId, Integer userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (product.getLikedByUsers().contains(user)) {
            product.getLikedByUsers().remove(user);
        } else {
            product.getLikedByUsers().add(user);
        }

        productRepository.save(product);
    }

    public List<ProductDTO> getProductsByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Kategoriya topilmadi"));

        List<Product> products = productRepository.findByCategory(category);

        return products.stream()
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .productBrand(product.getProductBrand())
                        .categoryId(product.getCategory().getId())
                        .attachmentIds(product.getAttachment().stream()
                                .map(Attachment::getId)
                                .collect(Collectors.toList()))
                        .colorIds(product.getColors().stream()
                                .map(ProductColor::getId)
                                .collect(Collectors.toList()))
                        .sizeIds(product.getSizes().stream()
                                .map(ProductSize::getId)
                                .collect(Collectors.toList()))
                        .likedByUsers(product.getLikedByUsers().stream()
                                .map(User::getId)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

}
