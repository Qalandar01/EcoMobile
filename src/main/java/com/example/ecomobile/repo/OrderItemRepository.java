package com.example.ecomobile.repo;

import com.example.ecomobile.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findOrderItemsByOrder_Id(Integer id);
}