package com.example.ecomobile.dto;

import lombok.Value;

@Value
public class SendMessageDTO {
    Integer sellerId;
    String message;
    Integer customerId;
}
