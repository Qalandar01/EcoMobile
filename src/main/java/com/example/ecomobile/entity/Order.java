package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import com.example.ecomobile.entity.Location;
import com.example.ecomobile.entity.OrderItem;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
@ToString(exclude = {"user", "location", "orderItems"})
public class Order extends BaseEntity {
    private Double total;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}