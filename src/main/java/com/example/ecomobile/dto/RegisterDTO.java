package com.example.ecomobile.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

@Value
public class RegisterDTO {
    @NotBlank(message = "Ism kiritilishi shart")
    String firstName;

    @NotBlank(message = "Familiya kiritilishi shart")
    String lastName;

    @NotBlank(message = "Email kiritilishi shart")
    @Email(message = "Email manzili to‘g‘ri formatda bo‘lishi kerak")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email manzili noto‘g‘ri formatda")
    String email;

    @NotBlank(message = "Parol kiritilishi shart")
    @Size(min = 8, message = "Parol kamida 8 belgidan iborat bo‘lishi kerak")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Parol kamida bitta kichik harf, bitta katta harf, bitta raqam va bitta maxsus belgi (@$!%*?&)ni o‘z ichiga olishi kerak"
    )
    String password;

    @NotNull(message = "Yosh kiritilishi shart")
    @Min(value = 18, message = "Yosh kamida 18 bo‘lishi kerak")
    Integer age;

    @NotBlank(message = "Jins kiritilishi shart")
    String gender;

    @NotBlank(message = "Telefon raqami kiritilishi shart")
    String phone;

    String code;
}
