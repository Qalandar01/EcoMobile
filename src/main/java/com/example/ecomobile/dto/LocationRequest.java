package com.example.ecomobile.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String region;
    private String district;
    private String street;
    private String home;
}