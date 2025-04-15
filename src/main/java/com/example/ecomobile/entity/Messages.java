package com.example.ecomobile.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @Column(name = "sent_at")
    @CreationTimestamp
    private LocalDateTime sentAt;

    // For Option 1 (direct relationship)
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chats chat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    // Getters and setters
}
