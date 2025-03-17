package com.example.ecomobile.controller;

import com.example.ecomobile.dto.OrderFilter;
import com.example.ecomobile.dto.OrderStatusDTO;
import com.example.ecomobile.service.OrderService;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public HttpEntity<?> getOrders(@ModelAttribute OrderFilter orderFilter ){
       return orderService.getAll(orderFilter);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{id}")
    public HttpEntity<?> getOrderItemsByOrderId(@PathVariable Integer id){
        return orderService.getItemsById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/status")
    public HttpEntity<?> changeOrderStatus(@RequestBody OrderStatusDTO statusDTO){
        return orderService.changeStatus(statusDTO);
    }
}
