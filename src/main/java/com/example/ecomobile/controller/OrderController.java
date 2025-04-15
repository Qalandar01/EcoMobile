package com.example.ecomobile.controller;

import com.example.ecomobile.dto.OrderDTO;
import com.example.ecomobile.dto.OrderItemDTO;
import com.example.ecomobile.dto.OrderStatusDTO;
import com.example.ecomobile.dto.OrderWithTotalPagesDTO;
import com.example.ecomobile.enums.NotificationType;
import com.example.ecomobile.service.NotificationService;
import com.example.ecomobile.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Add CORS support for development
public class OrderController {

    private final OrderService orderService;
    private final NotificationService notificationService;

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

    @GetMapping
    public ResponseEntity<OrderWithTotalPagesDTO> getAllOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(orderService.getAllOrders(search, date, page, size));
    }

    @PostMapping("/status")
    public ResponseEntity<Void> updateOrderStatus(@RequestBody OrderStatusDTO statusDTO) {
        orderService.updateOrderStatus(statusDTO.getOrderId(), statusDTO.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<OrderStatusDTO> getOrderStats() {
        return ResponseEntity.ok(orderService.getOrderStats());
    }

    // Test endpoint to send a notification to a user
    @PostMapping("/test-notification")
    public ResponseEntity<Void> sendTestNotification(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "Test notification message") String message) {
        notificationService.createNotification(
                userId,
                message,
                NotificationType.SYSTEM,
                null
        );
        return ResponseEntity.ok().build();
    }
}

