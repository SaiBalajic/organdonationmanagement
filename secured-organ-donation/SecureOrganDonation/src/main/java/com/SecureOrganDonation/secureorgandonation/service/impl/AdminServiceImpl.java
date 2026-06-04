package com.SecureOrganDonation.secureorgandonation.service.impl;



import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.model.Hospital;
import com.SecureOrganDonation.secureorgandonation.model.Notification;
import com.SecureOrganDonation.secureorgandonation.repository.HospitalRepository;
import com.SecureOrganDonation.secureorgandonation.repository.NotificationRepository;
import com.SecureOrganDonation.secureorgandonation.repository.UserRepository;
import com.SecureOrganDonation.secureorgandonation.service.AdminService;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    
    @Override
    public List<Hospital> getPendingHospitals() {
        return hospitalRepository.findAll()
                .stream()
                .filter(h -> "PENDING".equalsIgnoreCase(h.getStatus()))
                .toList();
    }

    @Override
    public Hospital approveHospital(UUID hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        hospital.setStatus("APPROVED");
        Hospital saved= hospitalRepository.save(hospital);
    
        Notification n = Notification.builder()
                .receiverRole("HOSPITAL")
                .receiverId(hospital.getHospitalId())
                .message("Your hospital has been approved by admin")
                .isRead(false)
                .build();
        notificationRepository.save(n);
        
        return saved;
    }
    
    
    
    
    

    @Override
    public Hospital rejectHospital(UUID hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        hospital.setStatus("REJECTED");
        Hospital saved = hospitalRepository.save(hospital);
    
        Notification n = Notification.builder()
                .receiverRole("HOSPITAL")
                .receiverId(hospital.getHospitalId())
                .message("Your hospital has been rejected by admin")
                .isRead(false)
                .build();
        notificationRepository.save(n);
        
        return saved;
    
    }

    @Override
    public void removeUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }
    
    @Override
    public void removeHospital(UUID hospitalId) {

        if (!hospitalRepository.existsById(hospitalId)) {
            throw new RuntimeException("Hospital not found");
        }

        long usersCount = userRepository.countByHospital_HospitalId(hospitalId);

        if (usersCount > 0) {
            throw new RuntimeException("Cannot delete hospital. Users are linked to this hospital.");
        }

        hospitalRepository.deleteById(hospitalId);
    }
    
    
}
