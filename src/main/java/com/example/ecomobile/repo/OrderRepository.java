package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}