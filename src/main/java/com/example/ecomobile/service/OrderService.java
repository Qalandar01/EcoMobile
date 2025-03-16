package com.example.ecomobile.service;

import com.example.ecomobile.dto.OrderDTO;
import com.example.ecomobile.entity.Order;
import com.example.ecomobile.entity.OrderItem;
import com.example.ecomobile.repo.OrderItemRepository;
import com.example.ecomobile.repo.OrderRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public HttpEntity<?> getAll() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : orders) {
            double sum = order.getOrderItems().stream().mapToDouble((item) -> item.getQuantity() * item.getProduct().getPrice()).sum();
            OrderDTO orderDTO = new OrderDTO(order.getStatus().name(),order.getId(),order.getOrderItems(),sum,order.getLocation().toString());
            orderDTOS.add(orderDTO);
        }

        return ResponseEntity.ok(orderDTOS);
    }

    public HttpEntity<?> getItemsById(Integer id) {
        List<OrderItem> items = orderItemRepository.findOrderItemsByOrder_Id(id);
        return ResponseEntity.ok(items);
    }
}
