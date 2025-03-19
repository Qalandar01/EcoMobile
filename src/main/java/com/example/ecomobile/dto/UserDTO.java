package com.example.ecomobile.dto;

import com.example.ecomobile.enums.Gender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Integer age;
    private Gender gender;
    private Integer attachmentId;
}