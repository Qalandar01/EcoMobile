package com.example.ecomobile.service;

import com.example.ecomobile.custom.Basket;
import com.example.ecomobile.dto.OrderDTO;
import com.example.ecomobile.dto.OrderItemDTO;
import com.example.ecomobile.dto.OrderStatusDTO;
import com.example.ecomobile.dto.OrderWithTotalPagesDTO;
import com.example.ecomobile.entity.*;
import com.example.ecomobile.enums.NotificationType;
import com.example.ecomobile.enums.OrderStatus;
import com.example.ecomobile.repo.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private NotificationService notificationService;

    public OrderDTO createOrderFromBasket(Integer userId, Integer locationId) {
        // 1. Get the basket
        Basket basket = basketService.getBasket(userId);
        if (basket == null || basket.getItems().isEmpty()) {
            throw new RuntimeException("Basket is empty!");
        }

        // 2. Check user and location
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // 3. Create new order
        Order order = new Order();
        order.setUser(user);
        order.setLocation(location);
        order.setStatus(OrderStatus.NEW);
        order.setOrderDate(LocalDateTime.now());

        // 4. Create OrderItems for each product in basket
        List<OrderItem> orderItems = basket.getItems().stream().map(basketItem -> {
            Product product = productRepository.findById(basketItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + basketItem.getProductId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setOrder(order); // Link to order
            return orderItem;
        }).collect(Collectors.toList());

        // 5. Set order items to order
        order.setOrderItems(orderItems);

        // 6. Calculate total
        Double total = orderItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        order.setTotal(total);

        // 7. Save order and items
        Order savedOrder = orderRepository.saveAndFlush(order);

        // 8. Create notification for new order
        notificationService.createNotification(
                userId,
                "Sizning #" + savedOrder.getId() + " raqamli buyurtmangiz qabul qilindi.",
                NotificationType.ORDER_STATUS,
                savedOrder.getId()
        );

        // 9. Clear basket
        basketService.clearBasket(userId);

        return mapToDTO(savedOrder);
    }

    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
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

    public OrderWithTotalPagesDTO getAllOrders(String search, String date, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderDate").descending());

        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String searchLower = "%" + search.toLowerCase() + "%";

                // Try to parse search as integer for ID search
                Integer searchId = null;
                try {
                    searchId = Integer.parseInt(search);
                } catch (NumberFormatException e) {
                    // Not an integer, ignore
                }

                List<Predicate> searchPredicates = new ArrayList<>();

                // Search by status
                searchPredicates.add(cb.like(cb.lower(root.get("status").as(String.class)), searchLower));

                // Search by ID if search is a number
                if (searchId != null) {
                    searchPredicates.add(cb.equal(root.get("id"), searchId));
                }

                // Search by location fields
                searchPredicates.add(cb.like(cb.lower(root.get("location").get("region")), searchLower));
                searchPredicates.add(cb.like(cb.lower(root.get("location").get("district")), searchLower));

                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }

            if (date != null && !date.isEmpty()) {
                try {
                    LocalDate filterDate = LocalDate.parse(date);
                    predicates.add(cb.equal(cb.function("DATE", LocalDate.class, root.get("orderDate")), filterDate));
                } catch (Exception e) {
                    // Invalid date format, ignore
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        List<OrderDTO> orders = orderPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new OrderWithTotalPagesDTO(orders, orderPage.getTotalPages());
    }

    public void updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);
            OrderStatus oldStatus = order.getStatus();

            // Only create notification if status actually changed
            if (oldStatus != newStatus) {
                order.setStatus(newStatus);
                orderRepository.save(order);

                // Create notification for status change
                String message = getStatusChangeMessage(orderId, newStatus);
                notificationService.createNotification(
                        order.getUser().getId(),
                        message,
                        NotificationType.ORDER_STATUS,
                        orderId
                );
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    private String getStatusChangeMessage(Integer orderId, OrderStatus status) {
        switch (status) {
            case PROCESSING:
                return "Sizning #" + orderId + " raqamli buyurtmangiz ishlov berilmoqda.";
            case SHIPPED:
                return "Sizning #" + orderId + " raqamli buyurtmangiz jo'natildi.";
            case DELIVERED:
                return "Sizning #" + orderId + " raqamli buyurtmangiz yetkazib berildi.";
            case CANCELLED:
                return "Sizning #" + orderId + " raqamli buyurtmangiz bekor qilindi.";
            default:
                return "Sizning #" + orderId + " raqamli buyurtmangiz holati yangilandi: " + status.name();
        }
    }

    public OrderStatusDTO getOrderStats() {
        Integer totalOrders = orderRepository.findAllCountOrders();
        Integer deliveredOrders = orderRepository.findTotalQuantity();
        Double totalRevenue = orderRepository.sumTotalRevenue();

        return new OrderStatusDTO(
                null, // No orderId for stats
                null, // No status for stats
                totalOrders,
                deliveredOrders,
                totalRevenue
        );
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

