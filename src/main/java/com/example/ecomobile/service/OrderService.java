package com.example.ecomobile.service;

import com.example.ecomobile.custom.Basket;
import com.example.ecomobile.custom.BasketItem;
import com.example.ecomobile.dto.OrderItemResponse;
import com.example.ecomobile.dto.OrderRequest;
import com.example.ecomobile.dto.OrderResponse;
import com.example.ecomobile.entity.*;
import com.example.ecomobile.enums.OrderStatus;
import com.example.ecomobile.exeption.ResourceNotFoundException;
import com.example.ecomobile.repo.LocationRepository;
import com.example.ecomobile.repo.OrderRepository;
import com.example.ecomobile.repo.ProductRepository;
import com.example.ecomobile.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final BasketService basketService;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;

    public OrderResponse createOrder(OrderRequest request, Integer userId) {
        // 1. Foydalanuvchini topish
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Savatchani olish
        Basket basket = basketService.getBasket(userId.toString());
        if (basket.getItems().isEmpty()) {
            throw new RuntimeException("Savatcha bo'sh");
        }

        // 3. Manzilni yaratish yoki yangilash
        Location location = locationRepository.save(
                Location.builder()
                        .region(request.getRegion())
                        .district(request.getDistrict())
                        .street(request.getStreet())
                        .home(request.getHome())
                        .build()
        );

        // 4. Buyurtma yaratish
        Order order = Order.builder()
                .user(user)
                .location(location)
                .status(OrderStatus.NEW)
                .orderItems(new ArrayList<>())
                .build();

        // 5. Buyurtma elementlarini qo'shish
        for (BasketItem basketItem : basket.getItems()) {
            Product product = productRepository.findById(basketItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Mahsulot topilmadi"));

            order.setOrderItems(List.of(
                    OrderItem.builder()
                            .product(product)
                            .quantity(basketItem.getQuantity())
                            .order(order)
                            .build())
            );
        }

        // 6. Buyurtmani saqlash va savatchani tozalash
        Order savedOrder = orderRepository.save(order);
        basketService.clearBasket(userId.toString());

        return mapToOrderResponse(savedOrder);
    }

//    private OrderResponse mapToOrderResponse(Order order) {
//        return OrderResponse.builder()
//                .id(order.getId())
//                .createdDate(order.getCreatedDate())
//                .status(order.getStatus())
//                .location(order.getLocation().toString())
//                .items(order.getOrderItems().stream()
//                        .map(this::mapToOrderItemResponse)
//                        .toList())
//                .build();
//    }


    public List<OrderResponse> getUserOrders(Integer userId) {
        // 1. Foydalanuvchini topish
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));

        // 2. Foydalanuvchining barcha buyurtmalarini olish
        List<Order> orders = orderRepository.findByUserId(userId);

        // 3. Har bir buyurtmani OrderResponse ga aylantirish
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    /**
     * Order ni OrderResponse ga aylantirish
     */
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .createdDate(LocalDateTime.now())
                .status(OrderStatus.NEW.name())
                .location(order.getLocation().toString())
                .items(order.getOrderItems().stream()
                        .map(this::mapToOrderItemResponse)
                        .toList())
                .totalAmount(calculateTotalAmount(order.getOrderItems())) // Umumiy summani hisoblash
                .build();
    }

    /**
     * OrderItem ni OrderItemResponse ga aylantirish
     */
    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getProduct().getPrice())
                .build();
    }

    /**
     * Buyurtma uchun umumiy summani hisoblash
     */
    private Double calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}