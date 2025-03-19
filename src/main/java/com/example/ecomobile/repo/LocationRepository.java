package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByUserId(Integer userId);

    Optional<Location> findByIdAndUserId(Integer locationId, Integer userId);
}