package com.example.ecomobile.service;

import com.example.ecomobile.dto.NotificationDTO;
import com.example.ecomobile.entity.Notification;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.enums.NotificationType;
import com.example.ecomobile.repo.NotificationRepository;
import com.example.ecomobile.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationEventService notificationEventService;

    public NotificationDTO createNotification(Integer userId, String message, NotificationType type, Integer referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .read(false)
                .createdAt(LocalDateTime.now())
                .referenceId(referenceId)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        NotificationDTO notificationDTO = mapToDTO(savedNotification);

        // Send real-time notification
        notificationEventService.sendNotification(userId, notificationDTO);

        return notificationDTO;
    }

    public List<NotificationDTO> getUserNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public NotificationDTO markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return mapToDTO(updatedNotification);
    }

    public void markAllAsRead(Integer userId) {
        notificationRepository.markAllAsRead(userId);
    }

    public Integer getUnreadCount(Integer userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .referenceId(notification.getReferenceId())
                .build();
    }
}

