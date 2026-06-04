package com.SecureOrganDonation.secureorgandonation.service.impl;


import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.model.AuditLog;
import com.SecureOrganDonation.secureorgandonation.repository.AuditLogRepository;
import com.SecureOrganDonation.secureorgandonation.security.JwtUtil;
import com.SecureOrganDonation.secureorgandonation.service.AuditLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void log(String action, String endpoint, String ip) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = "UNKNOWN";
        String role = "UNKNOWN";

        if (auth != null) {
            email = auth.getName();
        }

        AuditLog log = AuditLog.builder()
                .actorEmail(email)
                .actorRole(role)
                .action(action)
                .endpoint(endpoint)
                .ipAddress(ip)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }
}