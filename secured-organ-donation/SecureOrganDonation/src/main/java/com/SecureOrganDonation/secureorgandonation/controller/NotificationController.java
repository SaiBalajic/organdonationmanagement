package com.SecureOrganDonation.secureorgandonation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.SecureOrganDonation.secureorgandonation.model.Notification;
import com.SecureOrganDonation.secureorgandonation.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // GET /notifications -> all notifications for the authenticated actor
    @GetMapping
    public ResponseEntity<List<Notification>> getAll(Authentication authentication) {
        UUID receiverId = UUID.fromString(authentication.getCredentials().toString());
        return ResponseEntity.ok(notificationService.getAllNotifications(receiverId));
    }

    // GET /notifications/unread -> unread notifications for the authenticated actor
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnread(Authentication authentication) {
        UUID receiverId = UUID.fromString(authentication.getCredentials().toString());
        return ResponseEntity.ok(notificationService.getUnreadNotifications(receiverId));
    }

    // GET /notifications/unread/count -> unread count
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        UUID receiverId = UUID.fromString(authentication.getCredentials().toString());
        return ResponseEntity.ok(notificationService.countUnread(receiverId));
    }

    // PUT /notifications/{id}/read -> mark specific notification as read (must belong to same receiver)
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markRead(@PathVariable UUID notificationId, Authentication authentication) {
        UUID requesterId = UUID.fromString(authentication.getCredentials().toString());
        Notification n = notificationService.markAsRead(notificationId, requesterId);
        return ResponseEntity.ok(n);
    }
}