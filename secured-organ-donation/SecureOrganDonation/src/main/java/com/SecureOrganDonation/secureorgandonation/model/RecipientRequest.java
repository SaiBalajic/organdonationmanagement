package com.SecureOrganDonation.secureorgandonation.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recipient_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipientRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID recipientRequestId;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(nullable = false, length = 50)
    private String organRequired;

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN / MATCHED / CLOSED

    private LocalDateTime createdAt = LocalDateTime.now();
}
