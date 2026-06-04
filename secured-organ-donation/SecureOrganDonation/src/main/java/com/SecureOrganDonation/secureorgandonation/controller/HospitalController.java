package com.SecureOrganDonation.secureorgandonation.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SecureOrganDonation.secureorgandonation.dto.HospitalRegisterRequest;
import com.SecureOrganDonation.secureorgandonation.dto.LoginRequest;
import com.SecureOrganDonation.secureorgandonation.dto.LoginResponse;
import com.SecureOrganDonation.secureorgandonation.model.Hospital;
import com.SecureOrganDonation.secureorgandonation.model.User;
import com.SecureOrganDonation.secureorgandonation.repository.HospitalRepository;
import com.SecureOrganDonation.secureorgandonation.security.JwtUtil;
import com.SecureOrganDonation.secureorgandonation.service.HospitalService;
import com.SecureOrganDonation.secureorgandonation.util.EncryptionUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {

	private static final Logger log = LoggerFactory.getLogger(HospitalController.class);
	private final HospitalService hospitalService;
	private final HospitalRepository hospitalRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

		var hospital = hospitalRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Hospital not found"));

		if (!"APPROVED".equalsIgnoreCase(hospital.getStatus())) {
			throw new RuntimeException("Hospital is not approved");
		}

		if (!passwordEncoder.matches(request.getPassword(), hospital.getPasswordHash())) {
			throw new RuntimeException("Invalid password");
		}

		String token = jwtUtil.generateToken(hospital.getEmail(), "HOSPITAL", hospital.getHospitalId().toString());

		return ResponseEntity
				.ok(LoginResponse.builder().token(token).role("HOSPITAL").message("Hospital login successful").build());
	}

	@PostMapping("/register")
	public ResponseEntity<Hospital> register(@RequestBody HospitalRegisterRequest request) {
		Hospital saved = hospitalService.registerHospital(request);
		return ResponseEntity.ok(saved);
	}

	/*
	 * @GetMapping("/{hospitalId}/users/pending") public ResponseEntity<List<User>>
	 * getPendingUsers(@PathVariable UUID hospitalId) { return
	 * ResponseEntity.ok(hospitalService.getPendingUsers(hospitalId)); }
	 */

	
	
	@GetMapping("/users/pending")
    public ResponseEntity<List<User>> getPendingUsers(Authentication authentication) {
        try {
            if (authentication == null || authentication.getCredentials() == null) {
                return ResponseEntity.status(401).build();
            }

            String hospitalIdStr = authentication.getCredentials().toString();
            UUID hospitalId = UUID.fromString(hospitalIdStr);

            List<User> users = hospitalService.getPendingUsers(hospitalId);
            if (users == null || users.isEmpty()) {
                return ResponseEntity.ok(List.of()); // return empty list, not null
            }

            // Decrypt national IDs; protect per-user so one bad value won't break everything
            users.forEach(u -> {
                try {
                    if (u.getNationalId() != null) {
                        String decrypted = EncryptionUtil.decrypt(u.getNationalId());
                        u.setNationalId(decrypted);
                    }
                } catch (Exception ex) {
                    // log and keep original value (EncryptionUtil already tolerant, but safe)
                    log.warn("Failed decrypting nationalId for user {}: {}", u.getUserId(), ex.getMessage());
                }
            });

            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException iae) {
            // e.g., bad UUID
            return ResponseEntity.status(400).body(List.of());
        } catch (Exception e) {
            log.error("Error fetching pending users", e);
            return ResponseEntity.status(500).body(List.of());
        }
    }
	
	
	
	/*
	 * @GetMapping("/users/pending") public ResponseEntity<List<User>>
	 * getPendingUsers(Authentication authentication) {
	 * 
	 * 
	 * 
	 * Iterable<User> users = null; users.forEach(u -> {
	 * u.setNationalId(EncryptionUtil.decrypt(u.getNationalId())); });
	 * 
	 * 
	 * 
	 * String hospitalIdStr = authentication.getCredentials().toString(); UUID
	 * hospitalId = UUID.fromString(hospitalIdStr);
	 * 
	 * return ResponseEntity.ok(hospitalService.getPendingUsers(hospitalId)); }
	 */

	/*
	 * @GetMapping("/users/pending") public ResponseEntity<List<User>>
	 * getPendingUsers() { List<User> users = hospitalService.getPendingUsers(UUID
	 * userId); // Decrypt national IDs before returning users.forEach(u -> {
	 * u.setNationalId(EncryptionUtil.decrypt(u.getNationalId())); }); return
	 * ResponseEntity.ok(users); }
	 */

	/*
	 * @PutMapping("/user/{userId}/approve") public ResponseEntity<User>
	 * approveUser(@PathVariable UUID hospitalId, @PathVariable UUID userId) {
	 * 
	 * return ResponseEntity.ok(hospitalService.approveUser(hospitalId, userId)); }
	 */

	@PutMapping("/user/{userId}/approve")
	public ResponseEntity<?> approveUser(@PathVariable UUID userId, Authentication authentication) {
		try {
			if (authentication == null || authentication.getCredentials() == null) {
				return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
			}
			UUID hospitalId = UUID.fromString(authentication.getCredentials().toString());
			User updated = hospitalService.approveUser(hospitalId, userId);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException re) {
			/* log.warn("Business error approving user", re); */
			return ResponseEntity.status(400).body(Map.of("message", re.getMessage()));
		} catch (Exception e) {
			/* log.error("Error approving user", e); */
			return ResponseEntity.status(500).body(Map.of("message", "Something went wrong", "detail", e.getMessage()));
		}
	}

	/*
	 * @PutMapping("/{hospitalId}/user/{userId}/reject") public ResponseEntity<User>
	 * rejectUser(@PathVariable UUID hospitalId, @PathVariable UUID userId) { return
	 * ResponseEntity.ok(hospitalService.rejectUser(hospitalId, userId)); }
	 */

	@PutMapping("/user/{userId}/reject")
	public ResponseEntity<?> rejectUser(@PathVariable UUID userId, Authentication authentication) {
		try {
			if (authentication == null || authentication.getCredentials() == null) {
				return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
			}
			UUID hospitalId = UUID.fromString(authentication.getCredentials().toString());
			User updated = hospitalService.rejectUser(hospitalId, userId);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException re) {
			/* log.warn("Business error rejecting user", re); */
			return ResponseEntity.status(400).body(Map.of("message", re.getMessage()));
		} catch (Exception e) {
			/* log.error("Error rejecting user", e); */
			return ResponseEntity.status(500).body(Map.of("message", "Something went wrong", "detail", e.getMessage()));
		}
	}

}
