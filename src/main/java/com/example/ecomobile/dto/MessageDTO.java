package com.example.ecomobile.dto;

import lombok.Value;

@Value
public class MessageDTO {
    Integer senderId; // Kim yubordi
    String content;   // Xabar matni
    String sentAt;
}
