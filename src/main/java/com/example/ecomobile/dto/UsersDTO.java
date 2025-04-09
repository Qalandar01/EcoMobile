package com.example.ecomobile.dto;

import com.example.ecomobile.enums.Gender;
import com.example.ecomobile.enums.RoleName;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.List;
@Data
@Builder
public class UsersDTO {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Integer age;
    private Gender gender;
    private List<RoleName> roles; // Rolarni bir nechta bo'lgani uchun List sifatida saqlaymiz
}
