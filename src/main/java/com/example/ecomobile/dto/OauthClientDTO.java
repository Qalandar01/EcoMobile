package com.example.ecomobile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OauthClientDTO {
    private String email;
    private String picture;
    private String name;
}
