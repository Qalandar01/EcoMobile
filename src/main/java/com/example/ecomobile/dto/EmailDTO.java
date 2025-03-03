package com.example.ecomobile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class EmailDTO {
    @Email
    @NotBlank
    String email;
}
