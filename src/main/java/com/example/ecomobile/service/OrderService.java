package com.example.ecomobile.service;

import com.example.ecomobile.dto.OrderDTO;
import com.example.ecomobile.dto.OrderFilter;
import com.example.ecomobile.dto.OrderStatusDTO;
import com.example.ecomobile.dto.OrderWithTotalPagesDTO;
import com.example.ecomobile.entity.Order;
import com.example.ecomobile.entity.OrderItem;
import com.example.ecomobile.enums.OrderStatus;
import com.example.ecomobile.repo.OrderItemRepository;
import com.example.ecomobile.repo.OrderRepository;
import com.example.ecomobile.specifications.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public HttpEntity<?> getAll(OrderFilter orderFilter) {
        Specification<Order> orderSpecification = OrderSpecification.orderSpecification(orderFilter);
        PageRequest pageRequest = PageRequest.of(orderFilter.getPage()-1, orderFilter.getSize());
        Page<Order> all = orderRepository.findAll(orderSpecification, pageRequest);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : all.getContent()) {
            double sum = order.getOrderItems().stream().mapToDouble((item) -> item.getQuantity() * item.getProduct().getPrice()).sum();
            OrderDTO orderDTO = new OrderDTO(order.getStatus().name(),order.getId(),order.getOrderItems(),sum,order.getLocation().toString(),order.getCreatedAt());
            orderDTOS.add(orderDTO);
        }


        return ResponseEntity.ok(new OrderWithTotalPagesDTO(orderDTOS,all.getTotalPages()));
    }

    public HttpEntity<?> getItemsById(Integer id) {
        List<OrderItem> items = orderItemRepository.findOrderItemsByOrder_Id(id);
        return ResponseEntity.ok(items);
    }

    public HttpEntity<?> changeStatus(OrderStatusDTO statusDTO) {
        Optional<Order> optionalOrder = orderRepository.findById(statusDTO.getOrderId());
        if (optionalOrder.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Order order = optionalOrder.get();
        order.setStatus(OrderStatus.valueOf(statusDTO.getStatus()));
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }
}
