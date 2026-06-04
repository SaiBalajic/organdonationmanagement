package com.SecureOrganDonation.secureorgandonation.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SecureOrganDonation.secureorgandonation.dto.LoginRequest;
import com.SecureOrganDonation.secureorgandonation.dto.LoginResponse;
import com.SecureOrganDonation.secureorgandonation.model.Hospital;
import com.SecureOrganDonation.secureorgandonation.repository.AdminRepository;
import com.SecureOrganDonation.secureorgandonation.security.JwtUtil;
import com.SecureOrganDonation.secureorgandonation.service.AdminService;
import com.SecureOrganDonation.secureorgandonation.service.AuditLogService;
import com.SecureOrganDonation.secureorgandonation.service.MatchingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MatchingService matchingService;

    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request ) {

    	
    	
        var admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(admin.getEmail(), "ADMIN", admin.getAdminId().toString());


        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .role("ADMIN")
                .message("Admin login successful")
                .build());
    }

        
    
    
    @GetMapping("/hospitals/pending")
    public ResponseEntity<List<Hospital>> getPendingHospitals() {
        return ResponseEntity.ok(adminService.getPendingHospitals());
    }

    @PutMapping("/hospital/{hospitalId}/approve")
    public ResponseEntity<Hospital> approveHospital(@PathVariable UUID hospitalId) {
    	
    	
    	
        return ResponseEntity.ok(adminService.approveHospital(hospitalId));
    }

    @PutMapping("/hospital/{hospitalId}/reject")
    public ResponseEntity<Hospital> rejectHospital(@PathVariable UUID hospitalId) {
    	
    	
    	
        return ResponseEntity.ok(adminService.rejectHospital(hospitalId));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> removeUser(@PathVariable UUID userId) {
        
    	adminService.removeUser(userId);
        
    	
    	
        return ResponseEntity.ok("User removed successfully");
    }

    @DeleteMapping("/hospital/{hospitalId}")
    public ResponseEntity<String> removeHospital(@PathVariable UUID hospitalId) {
        
    	adminService.removeHospital(hospitalId);
        
    	
    	
        return ResponseEntity.ok("Hospital removed successfully");
    }
    
    
    @GetMapping("/hospitals/all")
    public ResponseEntity<List<Hospital>> getAllHospitals() {

        List<Hospital> hospitals = adminService.getAllHospitals();

        hospitals.forEach(h -> {
            try {
                if (h.getLicenseNumber() != null) {
                    h.setLicenseNumber(
                            com.SecureOrganDonation.secureorgandonation.util.EncryptionUtil
                                    .decrypt(h.getLicenseNumber()));
                }
            } catch (Exception e) {
                
            }
        });

        return ResponseEntity.ok(hospitals);
    }
    
    
    
    @PostMapping("/run-full-matching")
    public ResponseEntity<String> runFullMatching() {

        matchingService.runFullMatching();

        return ResponseEntity.ok("Full matching completed");
    }

    
    
    
}
