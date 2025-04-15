package com.example.ecomobile.service;

import com.example.ecomobile.dto.NotificationDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationEventService {

    // Store active SSE connections by userId
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    // Create a new SSE connection for a user
    public SseEmitter createEmitter(Integer userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Remove emitter on completion or timeout
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        // Add emitter to map
        emitters.put(userId, emitter);

        // Send initial connection established event
        try {
            emitter.send(SseEmitter.event()
                    .name("CONNECT")
                    .data("Connected to notification stream"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    // Send notification to a specific user
    public void sendNotification(Integer userId, NotificationDTO notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("NOTIFICATION")
                        .data(notification));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}

