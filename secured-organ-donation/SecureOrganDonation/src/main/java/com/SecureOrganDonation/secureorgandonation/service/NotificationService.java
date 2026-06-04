package com.SecureOrganDonation.secureorgandonation.service;

import java.util.List;
import java.util.UUID;
import com.SecureOrganDonation.secureorgandonation.model.Notification;

public interface NotificationService {
    List<Notification> getAllNotifications(UUID receiverId);
    List<Notification> getUnreadNotifications(UUID receiverId);
    long countUnread(UUID receiverId);
    Notification markAsRead(UUID notificationId, UUID requesterId);
}