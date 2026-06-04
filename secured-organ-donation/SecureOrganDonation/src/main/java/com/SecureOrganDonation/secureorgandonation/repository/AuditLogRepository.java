package com.SecureOrganDonation.secureorgandonation.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SecureOrganDonation.secureorgandonation.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
