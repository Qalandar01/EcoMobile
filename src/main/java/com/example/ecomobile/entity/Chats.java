package com.example.ecomobile.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Chats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    // Choose ONE of these approaches:

    // Option 1: Direct relationship (recommended)
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Messages> messages;


}
