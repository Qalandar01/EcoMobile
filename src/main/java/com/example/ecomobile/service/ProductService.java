package com.example.ecomobile.service;

import com.example.ecomobile.dto.ProductDTO;
import com.example.ecomobile.entity.*;
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
    private final ProductBrandRepository productBrandRepository; // qo'shildi
    private final RatingRepository ratingRepository;

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

        ProductBrand productBrand = productBrandRepository.findById(productDTO.getProductBrandId())
                .orElseThrow(() -> new RuntimeException("Brend topilmadi!"));

        ProductColor productColor = productColorRepository.findById(productDTO.getColorId())
                .orElseThrow(() -> new RuntimeException("Rang topilmadi!"));

        ProductSize productSize = productSizeRepository.findById(productDTO.getSizeId())
                .orElseThrow(() -> new RuntimeException("O'lcham topilmadi!"));

        List<Attachment> attachments = (productDTO.getAttachmentIds() != null && !productDTO.getAttachmentIds().isEmpty())
                ? attachmentRepository.findAllById(productDTO.getAttachmentIds())
                : new ArrayList<>();


        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .productBrand(productBrand)
                .attachment(attachments)
                .color(productColor)
                .size(productSize)
                .category(category)
                .amount(productDTO.getAmount())
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

        ProductBrand productBrand = productBrandRepository.findById(productDTO.getProductBrandId())
                .orElseThrow(() -> new RuntimeException("Brend topilmadi!"));
        product.setProductBrand(productBrand);

        product.setCategory(category);

        if (productDTO.getAttachmentIds() != null) {
            List<Attachment> attachments = attachmentRepository.findAllById(productDTO.getAttachmentIds());
            product.setAttachment(attachments);
        }

        if (productDTO.getColorId() != null) {
            ProductColor productColor = productColorRepository.findById(productDTO.getColorId())
                    .orElseThrow(() -> new RuntimeException("Rang topilmadi!"));
            product.setColor(productColor);
        }

        if (productDTO.getSizeId() != null) {
            ProductSize productSize = productSizeRepository.findById(productDTO.getSizeId())
                    .orElseThrow(() -> new RuntimeException("O'lcham topilmadi!"));
            product.setSize(productSize);
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Attachment> attachments = product.getAttachment();

        for (Attachment attachment : attachments) {
            AttachmentContent content = attachmentContentRepository.findByAttachmentId(attachment.getId());
            if (content != null) {
                attachmentContentRepository.delete(content);
            }
        }

        attachmentRepository.deleteAll(attachments);

        product.setColor(null);
        product.setSize(null);
        product.getLikedByUsers().clear();
        productRepository.save(product);

        productRepository.delete(product);
    }


    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .productBrandId(product.getProductBrand() != null ? product.getProductBrand().getId() : null)
                        .categoryId(product.getCategory().getId())
                        .attachmentIds(product.getAttachment() != null ?
                                product.getAttachment().stream().map(Attachment::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .colorId(product.getColor() != null ? product.getColor().getId() : null)
                        .sizeId(product.getSize() != null ? product.getSize().getId() : null)
                        .likedByUsers(product.getLikedByUsers() != null ?
                                product.getLikedByUsers().stream().map(User::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .amount(product.getAmount()) // Miqdorni qo'shdik
                        .productBrandId(product.getProductBrand() != null ? product.getProductBrand().getId() : null)
                        .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                        .colorId(product.getColor() != null ? product.getColor().getId() : null)
                        .sizeId(product.getSize() != null ? product.getSize().getId() : null)
                        .attachmentIds(product.getAttachment() != null ?
                                product.getAttachment().stream().map(Attachment::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .likedByUsers(product.getLikedByUsers() != null ?
                                product.getLikedByUsers().stream().map(User::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .productBrandName(product.getProductBrand() != null ? product.getProductBrand().getProductBrand() : "Noma’lum")
                        .categoryName(product.getCategory() != null ? product.getCategory().getName() : "Noma’lum")
                        .colorName(product.getColor() != null ? product.getColor().getProductColor() : "Noma’lum")
                        .sizeName(product.getSize() != null ? product.getSize().getProductSize() : "Noma’lum")
                        .build()
                );
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
                        .productBrandId(product.getProductBrand() != null ? product.getProductBrand().getId() : null)
                        .categoryId(product.getCategory().getId())
                        .attachmentIds(product.getAttachment() != null ?
                                product.getAttachment().stream().map(Attachment::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .colorId(product.getColor() != null ? product.getColor().getId() : null)
                        .sizeId(product.getSize() != null ? product.getSize().getId() : null)
                        .likedByUsers(product.getLikedByUsers() != null ?
                                product.getLikedByUsers().stream().map(User::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .build())
                .collect(Collectors.toList());
    }

    public Product findById(Integer id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Double getAverageRating(Integer productId) {
        return ratingRepository.findAverageRatingByProductId(productId).orElse(0.0);
    }

    public List<ProductDTO> getFavouriteProducts(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        List<Product> favouriteProducts = productRepository.findByLikedByUsersContaining(user);

        return favouriteProducts.stream()
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .productBrandId(product.getProductBrand() != null ? product.getProductBrand().getId() : null)
                        .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                        .attachmentIds(product.getAttachment() != null ?
                                product.getAttachment().stream().map(Attachment::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .colorId(product.getColor() != null ? product.getColor().getId() : null)
                        .sizeId(product.getSize() != null ? product.getSize().getId() : null)
                        .likedByUsers(product.getLikedByUsers() != null ?
                                product.getLikedByUsers().stream().map(User::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .productBrandName(product.getProductBrand() != null ? product.getProductBrand().getProductBrand() : "Noma’lum")
                        .categoryName(product.getCategory() != null ? product.getCategory().getName() : "Noma’lum")
                        .colorName(product.getColor() != null ? product.getColor().getProductColor() : "Noma’lum")
                        .sizeName(product.getSize() != null ? product.getSize().getProductSize() : "Noma’lum")
                        .build())
                .collect(Collectors.toList());
    }
}
