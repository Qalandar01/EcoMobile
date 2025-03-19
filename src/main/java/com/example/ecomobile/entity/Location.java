package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Location extends BaseEntity {

    private String region;
    private String district;
    private String street;
    private String home;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonBackReference // Bu yerda seriyalizatsiya siklini oldini olish uchun qo'shildi
    private User user;

    @Override
    public String toString() {
        return region + ", " + district + ", " + street + ", " + home;
    }
}
