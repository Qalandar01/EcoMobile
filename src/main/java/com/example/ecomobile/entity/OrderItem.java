package com.example.ecomobile.entity;


import com.example.ecomobile.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class OrderItem extends BaseEntity {
    private Integer quantity;

    @ManyToOne
    private Product product;

    @JsonBackReference
    @ManyToOne
    private Order order;
}
