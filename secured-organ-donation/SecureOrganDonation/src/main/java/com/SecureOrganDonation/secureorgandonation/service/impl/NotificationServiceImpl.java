package com.SecureOrganDonation.secureorgandonation.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.model.Notification;
import com.SecureOrganDonation.secureorgandonation.repository.NotificationRepository;
import com.SecureOrganDonation.secureorgandonation.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> getAllNotifications(UUID receiverId) {
        return notificationRepository.findByReceiverId(receiverId);
    }

    @Override
    public List<Notification> getUnreadNotifications(UUID receiverId) {
        return notificationRepository.findByReceiverIdAndIsRead(receiverId, false);
    }

    @Override
    public long countUnread(UUID receiverId) {
        return notificationRepository.countByReceiverIdAndIsRead(receiverId, false);
    }

    @Override
    public Notification markAsRead(UUID notificationId, UUID requesterId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        // authorize: only receiver can mark their notification as read
        if (!notification.getReceiverId().equals(requesterId)) {
            throw new RuntimeException("Not authorized to mark this notification");
        }
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
}