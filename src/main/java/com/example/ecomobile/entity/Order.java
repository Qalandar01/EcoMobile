package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {


    @ManyToOne
    private Location location;

    @ManyToOne
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
}
