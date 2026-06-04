package com.SecureOrganDonation.secureorgandonation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganMatch {

    @Id
    @GeneratedValue
    private UUID matchId;

    @ManyToOne
    private User donor;

    @ManyToOne
    private User recipient;

    private String organ;

    private String status; // PENDING ACCEPTED REJECTED

    private String acceptedBy;

    private LocalDateTime createdAt = LocalDateTime.now();
}