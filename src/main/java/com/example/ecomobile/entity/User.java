package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import com.example.ecomobile.enums.Gender;
import jakarta.persistence.*;
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
@Table(name = "users")
public class User extends BaseEntity {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToMany
    private List<Role> roles;

    @ManyToOne
    private Attachment attachment;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Location> locations ;
}
