package com.SecureOrganDonation.secureorgandonation.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SecureOrganDonation.secureorgandonation.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByReceiverId(UUID receiverId);
    List<Notification> findByReceiverIdAndIsRead(UUID receiverId, Boolean isRead);
    long countByReceiverIdAndIsRead(UUID receiverId, Boolean isRead);
}