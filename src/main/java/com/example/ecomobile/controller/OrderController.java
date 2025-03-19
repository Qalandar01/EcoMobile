package com.example.ecomobile.controller;

import com.example.ecomobile.dto.OrderRequest;
import com.example.ecomobile.dto.OrderResponse;
import com.example.ecomobile.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request,
            @RequestHeader("userId") Integer userId
    ) {
        return ResponseEntity.ok(orderService.createOrder(request, userId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @RequestHeader("userId") Integer userId
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
}