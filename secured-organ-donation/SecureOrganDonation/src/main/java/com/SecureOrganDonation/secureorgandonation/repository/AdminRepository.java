package com.SecureOrganDonation.secureorgandonation.repository;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SecureOrganDonation.secureorgandonation.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, UUID> {

    Optional<Admin> findByEmail(String email);
}

