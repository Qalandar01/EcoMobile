package com.example.ecomobile.service;

import com.example.ecomobile.custom.Basket;
import com.example.ecomobile.dto.OrderDTO;
import com.example.ecomobile.dto.OrderItemDTO;
import com.example.ecomobile.entity.*;
import com.example.ecomobile.enums.OrderStatus;
import com.example.ecomobile.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class OrderService {

    @Autowired
    private BasketService basketService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderDTO createOrderFromBasket(Integer userId, Integer locationId) {
        // 1. Savatchani olish
        Basket basket = basketService.getBasket(userId);
        if (basket == null || basket.getItems().isEmpty()) {
            throw new RuntimeException("Savatcha bo'sh!");
        }

        // 2. Foydalanuvchi va manzilni tekshirish
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Manzil topilmadi"));

        // 3. Yangi buyurtma obyektini yaratish
        Order order = new Order();
        order.setUser(user);
        order.setLocation(location);
        order.setStatus(OrderStatus.NEW);
        order.setOrderDate(LocalDateTime.now());

        // 4. Basketdagi har bir mahsulot uchun OrderItem yaratish
        List<OrderItem> orderItems = basket.getItems().stream().map(basketItem -> {
            Product product = productRepository.findById(basketItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Mahsulot topilmadi: " + basketItem.getProductId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setOrder(order); // Order bilan bog'lash
            return orderItem;
        }).collect(Collectors.toList());

        // 5. Yaratilgan orderItemlarni order obyektiga o'rnatish
        order.setOrderItems(orderItems);

        // 6. Umumiy summani hisoblash
        Double total = orderItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        order.setTotal(total);

        // 7. Order va uning itemlarini cascade orqali saqlash
        // saveAndFlush orqali order id'si darhol generatsiya qilinishini ta'minlaymiz
        Order savedOrder = orderRepository.saveAndFlush(order);
        System.out.println("savedOrder = " + savedOrder);

        // 8. Savatchani tozalash
        basketService.clearBasket(userId);

        return mapToDTO(savedOrder);
    }


    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Buyurtma topilmadi"));
        return mapToDTO(order);
    }

    public List<OrderDTO> getOrdersByUser(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderItemDTO> getOrderItems(Integer orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream()
                .map(this::mapToItemDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .total(order.getTotal())
                .locationId(order.getLocation().getId())
                .date(order.getOrderDate().toLocalDate())
                .orderItems(order.getOrderItems().stream()
                        .map(this::mapToItemDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemDTO mapToItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}