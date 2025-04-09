package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
//
//    @Query(value = """
// SELECT count(o.id) FROM orders o WHERE o.status = 'DELIVERED';
//""",nativeQuery = true)
//    Integer findTotalQuantity();
//
//    @Query(value = """
// select count(s.quantity) from orders o join order_item s on o.id = s.order_id
//""",nativeQuery = true)
//    Integer findAllCountOrders();

    @Query(value = "SELECT COALESCE(SUM(o.total), 0) FROM orders o", nativeQuery = true)
    Double sumTotalRevenue();

    @Query(value = "SELECT COUNT(o.id) FROM orders o", nativeQuery = true)
    Integer findAllCountOrders();

    @Query(value = "SELECT COUNT(o.id) FROM orders o WHERE o.status = 'DELIVERED'", nativeQuery = true)
    Integer findTotalQuantity();

    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}