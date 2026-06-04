package com.SecureOrganDonation.secureorgandonation.service.impl;

import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.service.UserService;
import com.SecureOrganDonation.secureorgandonation.util.EncryptionUtil;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.dto.UserRegisterRequest;
import com.SecureOrganDonation.secureorgandonation.model.Hospital;
import com.SecureOrganDonation.secureorgandonation.model.User;
import com.SecureOrganDonation.secureorgandonation.repository.HospitalRepository;
import com.SecureOrganDonation.secureorgandonation.repository.UserRepository;
import com.SecureOrganDonation.secureorgandonation.service.UserService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegisterRequest request) {
        // 1) Check email uniqueness
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new RuntimeException("User email already exists");
        });

        // 2) Duplicate national ID check (decrypt each stored nationalId safely)
        if (request.getNationalId() != null && !request.getNationalId().isBlank()) {
            List<User> all = userRepository.findAll();
            for (User us : all) {
                try {
                    String storedEnc = us.getNationalId();
                    if (storedEnc == null) continue;
                    String decrypted = EncryptionUtil.decrypt(storedEnc);
                    if (decrypted != null && decrypted.equals(request.getNationalId())) {
                        throw new RuntimeException("National ID already exists");
                    }
                } catch (Exception ex) {
                    // tolerate decryption errors for single rows (legacy/invalid). continue scanning others.
                }
            }
        }

        // 3) Find hospital by license (decrypt license numbers). Small scale acceptable.
        Hospital hospital = hospitalRepository.findAll().stream()
            .filter(hos -> {
                try {
                    if (hos.getLicenseNumber() == null) return false;
                    return EncryptionUtil.decrypt(hos.getLicenseNumber())
                        .equals(request.getHospitalLicenseNumber());
                } catch (Exception ex) {
                    return false;
                }
            })
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Hospital not found with given license number"));

        if (!"APPROVED".equalsIgnoreCase(hospital.getStatus())) {
            throw new RuntimeException("Hospital is not approved yet");
        }

        if (request.getConsent() == null || !request.getConsent()) {
            throw new RuntimeException("Consent is required");
        }

        // 4) Build user and set organ fields according to role.
        // Handle legacy cases where frontend might have sent organ in wrong field.
        String role = request.getUserRoleType() == null ? "USER" : request.getUserRoleType().toUpperCase();

        String organsToDonate = null;
        String organsToReceive = null;

        if ("DONOR".equals(role)) {
            // prefer the explicit donor field, fallback to recipient field if donor field empty (legacy)
            if (request.getOrgansToDonate() != null && !request.getOrgansToDonate().isBlank()) {
                organsToDonate = request.getOrgansToDonate().trim();
            } else if (request.getOrgansToReceive() != null && !request.getOrgansToReceive().isBlank()) {
                // frontend might have put value into organsToReceive by mistake
                organsToDonate = request.getOrgansToReceive().trim();
            }
        } else if ("RECIPIENT".equals(role)) {
            // prefer explicit recipient field, fallback to donor field if recipient field empty (legacy)
            if (request.getOrgansToReceive() != null && !request.getOrgansToReceive().isBlank()) {
                organsToReceive = request.getOrgansToReceive().trim();
            } else if (request.getOrgansToDonate() != null && !request.getOrgansToDonate().isBlank()) {
                // frontend might have put requested organ into organsToDonate
                organsToReceive = request.getOrgansToDonate().trim();
            }
        } else if ("BOTH".equals(role)) {
            if (request.getOrgansToDonate() != null && !request.getOrgansToDonate().isBlank()) {
                organsToDonate = request.getOrgansToDonate().trim();
            }
            if (request.getOrgansToReceive() != null && !request.getOrgansToReceive().isBlank()) {
                organsToReceive = request.getOrgansToReceive().trim();
            }
            // if one of them is missing but the other present, don't overwrite — keep whatever was provided
        } else {
            // default: keep both if provided (safe)
            if (request.getOrgansToDonate() != null && !request.getOrgansToDonate().isBlank()) {
                organsToDonate = request.getOrgansToDonate().trim();
            }
            if (request.getOrgansToReceive() != null && !request.getOrgansToReceive().isBlank()) {
                organsToReceive = request.getOrgansToReceive().trim();
            }
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .dob(request.getDob())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                // encrypt national ID before storing
                .nationalId(EncryptionUtil.encrypt(request.getNationalId()))
                .userRoleType(role)
                .email(request.getEmail())
                .phone(request.getPhone())
                .organsToDonate(organsToDonate)
                .organsToReceive(organsToReceive)
                .hospital(hospital)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status("PENDING")
                .role("USER")
                .consent(true)
                .build();

        return userRepository.save(user);
    }
}
	

