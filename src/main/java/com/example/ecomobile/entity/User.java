package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import com.example.ecomobile.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
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

    @ManyToMany
    private List<Product> favoriteProducts;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Location> locations ;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
