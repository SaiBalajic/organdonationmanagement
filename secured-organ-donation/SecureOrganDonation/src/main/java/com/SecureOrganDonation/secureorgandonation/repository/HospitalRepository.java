package com.SecureOrganDonation.secureorgandonation.repository;



import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SecureOrganDonation.secureorgandonation.model.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, UUID> {

    Optional<Hospital> findByEmail(String email);

    Optional<Hospital> findByLicenseNumber(String licenseNumber);
}

