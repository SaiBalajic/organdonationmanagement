package com.SecureOrganDonation.secureorgandonation.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(length = 20)
    private String gender;

    @Column(length = 10)
    private String bloodGroup;
    
    @Column(nullable = false, unique = true, length = 30)
    private String nationalId;

    @Column(nullable = false, length = 20)
    private String userRoleType;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String organsToDonate; // store as comma-separated for now

    @Column(length = 255)
    private String organsToReceive;
    
    @Column(length = 150)
    private String emergencyContact;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING / APPROVED / REJECTED

    @Column(nullable = false)
    private String role = "USER";

    @Column(nullable = false)
    private Boolean consent;

    private LocalDateTime createdAt = LocalDateTime.now();
}

