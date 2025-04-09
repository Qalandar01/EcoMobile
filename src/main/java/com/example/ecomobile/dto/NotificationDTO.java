package com.example.ecomobile.dto;

import com.example.ecomobile.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Integer id;
    private Integer userId;
    private String message;
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;
    private Integer referenceId;
}

