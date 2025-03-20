package com.example.ecomobile.controller;

import com.example.ecomobile.dto.OrderDTO;
import com.example.ecomobile.dto.OrderItemDTO;
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

    @PostMapping("/create-from-basket")
    public ResponseEntity<OrderDTO> createFromBasket(
            @RequestHeader("userId") Integer userId,
            @RequestParam Integer locationId
    ) {
        OrderDTO orderDTO = orderService.createOrderFromBasket(userId, locationId);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getById(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDTO>> getItems(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }
}