package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}