package com.example.ecomobile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class EmailDTO {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    String email;
}
