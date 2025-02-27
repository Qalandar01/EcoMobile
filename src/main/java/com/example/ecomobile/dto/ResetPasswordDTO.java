package com.example.ecomobile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String confirmationCode;

    @NotBlank
    private String newPassword;
}
