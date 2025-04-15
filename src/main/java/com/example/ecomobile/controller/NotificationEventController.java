package com.example.ecomobile.controller;

import com.example.ecomobile.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notification-events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationEventController {

    private final NotificationEventService notificationEventService;

    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Integer userId) {
        return notificationEventService.createEmitter(userId);
    }
}

