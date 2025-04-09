package com.example.ecomobile.dto;

import lombok.Value;

import java.time.LocalDateTime;


@Value
public class MessagesDTO {
    Integer id;
    String content;
    Integer senderId;
    LocalDateTime sentAt;
}
