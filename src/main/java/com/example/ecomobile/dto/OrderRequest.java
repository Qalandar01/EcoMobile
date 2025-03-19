package com.example.ecomobile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequest {
    private String region;
    private String district;
    private String street;
    private String home;
}
