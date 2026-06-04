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
@Table(name = "organ_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID requestId;

    @ManyToOne
    @JoinColumn(name = "donor_id")
    private User donor;

    @ManyToOne
    @JoinColumn(name = "source_hospital_id")
    private Hospital sourceHospital;

    @ManyToOne
    @JoinColumn(name = "target_hospital_id")
    private Hospital targetHospital;

    @Column(nullable = false, length = 50)
    private String organ;

    @Column(nullable = false)
    private String status = "INITIATED"; // INITIATED / ACCEPTED / REJECTED

    private LocalDateTime createdAt = LocalDateTime.now();
}