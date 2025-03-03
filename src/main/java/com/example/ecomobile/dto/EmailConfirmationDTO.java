package com.example.ecomobile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailConfirmationDTO {
    @Email
    private String email;
    @NotBlank
    private String code;
}
