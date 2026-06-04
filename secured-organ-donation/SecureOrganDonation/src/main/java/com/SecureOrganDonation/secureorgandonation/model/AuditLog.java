package com.SecureOrganDonation.secureorgandonation.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID auditId;

    private String actorRole;
    private String actorEmail;
    
    private String httpMethod;
    private String actorId;

    private String action;
    private String endpoint;

    private String ipAddress;

    private LocalDateTime createdAt;
}