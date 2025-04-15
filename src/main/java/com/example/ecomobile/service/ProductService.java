package com.example.ecomobile.service;

import com.example.ecomobile.dto.ProductDTO;
import com.example.ecomobile.dto.ProductVariantDTO;
import com.example.ecomobile.entity.*;
import com.example.ecomobile.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
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
    private final ProductBrandRepository productBrandRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<ProductDTO> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .amount(product.getAmount())
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
                        .productBrand(product.getProductBrand() != null ? product.getProductBrand().getProductBrand() : "Noma'lum")
                        .categoryName(product.getCategory() != null ? product.getCategory().getName() : "Noma'lum")
                        .productColor(product.getColor() != null ? product.getColor().getProductColor() : "Noma'lum")
                        .productSize(product.getSize() != null ? product.getSize().getProductSize() : "Noma'lum")
                        .build()
                );
    }

    public List<ProductVariantDTO> getProductVariants(String productName) {
        String sql = """
        WITH product_sizes AS (
            SELECT
                p.name,
                array_agg(DISTINCT s.product_size) as available_sizes,
                array_agg(DISTINCT c.product_color) as available_colors,
                array_agg(pa.attachment_id) as attachment_ids
            FROM Product p
                     JOIN product_color c ON p.color_id = c.id
                     JOIN product_size s ON p.size_id = s.id
                     JOIN public.product_attachment pa ON p.id = pa.product_id
            WHERE p.name = ?
            GROUP BY p.name
        )
        SELECT
            name,
            available_sizes,
            available_colors,
            attachment_ids
        FROM product_sizes
        ORDER BY name
    """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, productName);

        List<ProductVariantDTO> variants = new ArrayList<>();
        for (Map<String, Object> row : results) {
            ProductVariantDTO variant = new ProductVariantDTO();
            variant.setName((String) row.get("name"));

            // Proper PostgreSQL array handling
            try {
                // Convert available_sizes (String array)
                java.sql.Array sizesSqlArray = (java.sql.Array) row.get("available_sizes");
                String[] sizesArray = (String[]) sizesSqlArray.getArray();
                variant.setAvailable_sizes(Arrays.asList(sizesArray));

                // Convert available_colors (String array)
                java.sql.Array colorsSqlArray = (java.sql.Array) row.get("available_colors");
                String[] colorsArray = (String[]) colorsSqlArray.getArray();
                variant.setAvailable_colors(Arrays.asList(colorsArray));

                // Convert attachment_ids (Integer array)
                java.sql.Array attachmentsSqlArray = (java.sql.Array) row.get("attachment_ids");
                Integer[] attachmentsArray = (Integer[]) attachmentsSqlArray.getArray();
                variant.setAttachment_ids(Arrays.asList(attachmentsArray));
            } catch (SQLException e) {
                throw new RuntimeException("Error converting PostgreSQL arrays", e);
            }

            variants.add(variant);
        }

        return variants;
    }

    public Optional<ProductDTO> getProductVariant(String name, String color, String size) {
        // Mahsulot nomi, rangi va o'lchamiga qarab variantni topish
        return productRepository.findByNameAndColorAndSize(name, color, size)
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .amount(product.getAmount())
                        .productColor(product.getColor() != null ? product.getColor().getProductColor() : "Noma'lum")
                        .productSize(product.getSize() != null ? product.getSize().getProductSize() : "Noma'lum")
                        .build()
                );
    }

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
                .orElseThrow(() -> new RuntimeException("Kategoriya ID noto'g'ri yoki topilmadi!"));

        ProductBrand productBrand = productBrandRepository.findById(productDTO.getProductBrandId())
                .orElseThrow(() -> new RuntimeException("Brend topilmadi!"));

        ProductColor productColor = productColorRepository.findById(productDTO.getColorId())
                .orElseThrow(() -> new RuntimeException("Rang topilmadi!"));

        ProductSize productSize = productSizeRepository.findById(productDTO.getSizeId())
                .orElseThrow(() -> new RuntimeException("O'lcham topilmadi!"));

        List<Attachment> attachments = (productDTO.getAttachmentIds() != null && !productDTO.getAttachmentIds().isEmpty())
                ? attachmentRepository.findAllById(productDTO.getAttachmentIds())
                : new ArrayList<>();

        User user = userRepository.findById(productDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found!"));

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
                .users(user)
                .build();
        productRepository.save(product);
    }

    public Product updateProduct(Integer id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mahsulot topilmadi"));

        // Update basic fields
        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }

        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }

        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }

        // Update amount
        if (productDTO.getAmount() != null) {
            product.setAmount(productDTO.getAmount());
        }

        // Update category if provided
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Kategoriya topilmadi"));
            product.setCategory(category);
        }

        // Update brand if provided
        if (productDTO.getProductBrandId() != null) {
            ProductBrand productBrand = productBrandRepository.findById(productDTO.getProductBrandId())
                    .orElseThrow(() -> new RuntimeException("Brend topilmadi!"));
            product.setProductBrand(productBrand);
        }

        // Update attachments if provided
        if (productDTO.getAttachmentIds() != null && !productDTO.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = attachmentRepository.findAllById(productDTO.getAttachmentIds());
            product.setAttachment(attachments);
        }

        // Update color if provided
        if (productDTO.getColorId() != null) {
            ProductColor productColor = productColorRepository.findById(productDTO.getColorId())
                    .orElseThrow(() -> new RuntimeException("Rang topilmadi!"));
            product.setColor(productColor);
        }

        // Update size if provided
        if (productDTO.getSizeId() != null) {
            ProductSize productSize = productSizeRepository.findById(productDTO.getSizeId())
                    .orElseThrow(() -> new RuntimeException("O'lcham topilmadi!"));
            product.setSize(productSize);
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mahsulot topilmadi"));

        // 1. Remove product likes references
        if (product.getLikedByUsers() != null && !product.getLikedByUsers().isEmpty()) {
            product.getLikedByUsers().clear();
            productRepository.save(product);
        }

        // 2. Delete attachments and their contents
        if (product.getAttachment() != null && !product.getAttachment().isEmpty()) {
            for (Attachment attachment : product.getAttachment()) {
                // Delete attachment content first
                Optional<AttachmentContent> content = Optional.ofNullable(attachmentContentRepository.findByAttachmentId(attachment.getId()));
                content.ifPresent(attachmentContentRepository::delete);

                // Then delete the attachment
                attachmentRepository.delete(attachment);
            }
        }

        // 3. Finally delete the product
        productRepository.delete(product);
    }





    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(p -> ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .description(p.getDescription())
                .attachmentIds(p.getAttachment() != null ?
                        p.getAttachment().stream().map(Attachment::getId).collect(Collectors.toList())
                        : new ArrayList<>())
                .likedByUsers(p.getLikedByUsers() != null ?
                        p.getLikedByUsers().stream().map(User::getId).collect(Collectors.toList())
                        : new ArrayList<>())
                .amount(p.getAmount())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : "N/A")
                .productBrandId(p.getProductBrand() != null ? p.getProductBrand().getId() : null)
                .productBrand(p.getProductBrand() != null ? p.getProductBrand().getProductBrand() : "N/A")
                .sizeId(p.getSize() != null ? p.getSize().getId() : null)
                .productSize(p.getSize() != null ? p.getSize().getProductSize() : "N/A")
                .colorId(p.getColor() != null ? p.getColor().getId() : null)
                .productColor(p.getColor() != null ? p.getColor().getProductColor() : "N/A")
                .build()
        ).collect(Collectors.toList());
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
                        .productBrand(product.getProductBrand() != null ? product.getProductBrand().getProductBrand() : "Noma'lum")
                        .categoryName(product.getCategory() != null ? product.getCategory().getName() : "Noma'lum")
                        .productColor(product.getColor() != null ? product.getColor().getProductColor() : "Noma'lum")
                        .productSize(product.getSize() != null ? product.getSize().getProductSize() : "Noma'lum")
                        .build())
                .collect(Collectors.toList());
    }



    public List<ProductDTO> searchProducts(String query, int page, int size) {
        // Convert query to lowercase for case-insensitive search
        String searchQuery = "%" + query.toLowerCase() + "%";

        // Find products that match the search query in name or description
        List<Product> products = productRepository.findByNameOrDescriptionContainingIgnoreCase(searchQuery, page, size);

        // Convert to DTOs
        return products.stream()
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .attachmentIds(product.getAttachment() != null ?
                                product.getAttachment().stream().map(Attachment::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .likedByUsers(product.getLikedByUsers() != null ?
                                product.getLikedByUsers().stream().map(User::getId).collect(Collectors.toList())
                                : new ArrayList<>())
                        .amount(product.getAmount())
                        .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                        .categoryName(product.getCategory() != null ? product.getCategory().getName() : "N/A")
                        .productBrandId(product.getProductBrand() != null ? product.getProductBrand().getId() : null)
                        .productBrand(product.getProductBrand().getProductBrand() != null ? product.getProductBrand().getProductBrand() : "N/A")
                        .sizeId(product.getSize() != null ? product.getSize().getId() : null)
                        .productSize(product.getSize() != null ? product.getSize().getProductSize() : "N/A")
                        .colorId(product.getColor() != null ? product.getColor().getId() : null)
                        .productColor(product.getColor() != null ? product.getColor().getProductColor() : "N/A")
                        .build())
                .collect(Collectors.toList());
    }
}

