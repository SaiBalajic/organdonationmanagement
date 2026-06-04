package com.SecureOrganDonation.secureorgandonation.service;

public interface AuditLogService {
    void log(String action, String endpoint, String ip);
}
