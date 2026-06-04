package com.SecureOrganDonation.secureorgandonation.model;



import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospitals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID hospitalId;

    @Column(nullable = false, length = 200)
    private String hospitalName;

    @Column(nullable = false, unique = true, length = 100)
    private String licenseNumber;

    @Column(length = 50)
    private String hospitalType;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 100)
    private String authorizedPerson;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING / APPROVED / REJECTED

    @Column(nullable = false)
    private String role = "HOSPITAL";

    private LocalDateTime createdAt = LocalDateTime.now();
}
