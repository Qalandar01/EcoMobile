package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Rating;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    @Query("SELECT AVG(r.starsCount) FROM Rating r WHERE r.product.id = :productId")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Integer productId);
}