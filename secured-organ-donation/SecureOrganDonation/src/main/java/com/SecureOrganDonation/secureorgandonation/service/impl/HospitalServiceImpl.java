package com.SecureOrganDonation.secureorgandonation.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.dto.HospitalRegisterRequest;
import com.SecureOrganDonation.secureorgandonation.model.Hospital;
import com.SecureOrganDonation.secureorgandonation.model.Notification;
import com.SecureOrganDonation.secureorgandonation.model.User;
import com.SecureOrganDonation.secureorgandonation.repository.HospitalRepository;
import com.SecureOrganDonation.secureorgandonation.repository.NotificationRepository;
import com.SecureOrganDonation.secureorgandonation.repository.UserRepository;
import com.SecureOrganDonation.secureorgandonation.service.HospitalService;
import com.SecureOrganDonation.secureorgandonation.service.MatchingService;
import com.SecureOrganDonation.secureorgandonation.util.EncryptionUtil;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

	private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;
    private final MatchingService matchingService;

    //@Override
    public Hospital registerHospital(HospitalRegisterRequest request) {
        hospitalRepository.findByEmail(request.getEmail()).ifPresent(h -> {
            throw new RuntimeException("Hospital email already exists");
        });

        // Check duplicate license by decrypting existing hospitals
        hospitalRepository.findAll().forEach(hos -> {
            String decrypted = EncryptionUtil.decrypt(hos.getLicenseNumber());
            if (decrypted.equals(request.getLicenseNumber())) {
                throw new RuntimeException("License number already exists");
            }
        });

        Hospital hospital = Hospital.builder()
                .hospitalName(request.getHospitalName())
                .address(request.getAddress())
                // encrypt license before storing
                .licenseNumber(EncryptionUtil.encrypt(request.getLicenseNumber()))
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .authorizedPerson(request.getAuthorizedPerson())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status("PENDING")
                .role("HOSPITAL")
                .build();

        return hospitalRepository.save(hospital);
    }

    @Override
    public List<User> getPendingUsers(UUID hospitalId) {
        // Prefer repository filtering (let DB do the work) - example method assumed:
        List<User> users = userRepository.findByHospital_HospitalIdAndStatus(hospitalId, "PENDING");
        if (users == null) {
            return List.of();
        }
        return users;
    }

    @Override
    public User approveUser(UUID hospitalId, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getHospital() == null || !user.getHospital().getHospitalId().equals(hospitalId)) {
            throw new RuntimeException("User does not belong to this hospital");
        }

        user.setStatus("APPROVED");
        User saved = userRepository.save(user);
        
        Notification n = Notification.builder()
                .receiverRole("USER")
                .receiverId(saved.getUserId())
                .message("Your account has been approved by hospital: " + saved.getHospital().getHospitalName())
                .isRead(false)
                .build();
        notificationRepository.save(n);
        
        matchingService.runMatchingForUser(user.getUserId());

        return saved;
    }

    @Override
    public User rejectUser(UUID hospitalId, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getHospital() == null || !user.getHospital().getHospitalId().equals(hospitalId)) {
            throw new RuntimeException("User does not belong to this hospital");
        }

        user.setStatus("REJECTED");
        User saved = userRepository.save(user);
        
        Notification n = Notification.builder()
                .receiverRole("USER")
                .receiverId(saved.getUserId())
                .message("Your account has been rejected by hospital: " + saved.getHospital().getHospitalName())
                .isRead(false)
                .build();
        notificationRepository.save(n);

        return saved;
        
    }

	

}
