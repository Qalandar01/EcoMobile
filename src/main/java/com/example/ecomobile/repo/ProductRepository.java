package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Category;
import com.example.ecomobile.entity.Product;
import com.example.ecomobile.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
//    List<Product> findByCategory(Category category);
//
//    @Query("select count(p) from Product p where p.category.id = :categoryId ")
//    Long countProductsByCategory(@Param("categoryId") Integer categoryId);
//
//    List<Product> findByLikedByUsersContaining(User user);
//
//    @Query("select count(p.id) from Product p")
//    Integer findAllCountProducts();
//
List<Product> findByCategory(Category category);

    @Query("select count(p) from Product p where p.category.id = :categoryId")
    Long countProductsByCategory(@Param("categoryId") Integer categoryId);

    List<Product> findByLikedByUsersContaining(User user);

    @Query("select count(p.id) from Product p")
    Integer findAllCountProducts();

    @Query("select p from Product p where p.name = :name and p.color.productColor = :color and p.size.productSize = :size")
    Optional<Product> findByNameAndColorAndSize(
            @Param("name") String name,
            @Param("color") String color,
            @Param("size") String size);

    @Query("select avg(r.starsCount) from Rating r where r.product.id = :productId")
    Double getAverageRating(@Param("productId") Integer productId);
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:query) OR LOWER(p.description) LIKE LOWER(:query)")
    List<Product> findByNameOrDescriptionContainingIgnoreCase(
            @Param("query") String query,
            Pageable pageable);

    // Helper method to use with pagination
    default List<Product> findByNameOrDescriptionContainingIgnoreCase(String query, int page, int size)
    {
        Pageable
                pageable = PageRequest.of(page, size);
        return findByNameOrDescriptionContainingIgnoreCase(query, pageable);
    }
}