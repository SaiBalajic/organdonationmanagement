package com.SecureOrganDonation.secureorgandonation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SecureOrganDonation.secureorgandonation.dto.LoginRequest;
import com.SecureOrganDonation.secureorgandonation.dto.LoginResponse;
import com.SecureOrganDonation.secureorgandonation.dto.UserRegisterRequest;
import com.SecureOrganDonation.secureorgandonation.model.User;
import com.SecureOrganDonation.secureorgandonation.repository.UserRepository;
import com.SecureOrganDonation.secureorgandonation.security.JwtUtil;
import com.SecureOrganDonation.secureorgandonation.service.MatchingService;
import com.SecureOrganDonation.secureorgandonation.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MatchingService matchingService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"APPROVED".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("User is not approved");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), "USER", user.getUserId().toString());


        
		matchingService.runMatchingForUser(user.getUserId());
        
        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .role("USER")
                .message("User login successful")
                .build());
    }

    

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterRequest request) {
        User saved = userService.registerUser(request);
        return ResponseEntity.ok(saved);
    }
}
