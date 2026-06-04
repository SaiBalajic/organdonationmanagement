package com.SecureOrganDonation.secureorgandonation.service.impl;



import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.dto.RecipientRequestCreateRequest;
import com.SecureOrganDonation.secureorgandonation.model.Notification;
import com.SecureOrganDonation.secureorgandonation.model.OrganMatch;
import com.SecureOrganDonation.secureorgandonation.model.RecipientRequest;
import com.SecureOrganDonation.secureorgandonation.model.User;
import com.SecureOrganDonation.secureorgandonation.repository.NotificationRepository;
import com.SecureOrganDonation.secureorgandonation.repository.OrganMatchRepository;
import com.SecureOrganDonation.secureorgandonation.repository.RecipientRequestRepository;
import com.SecureOrganDonation.secureorgandonation.repository.UserRepository;
import com.SecureOrganDonation.secureorgandonation.service.RecipientRequestService;


import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class RecipientRequestServiceImpl implements RecipientRequestService {

	private final OrganMatchRepository matchRepository;
	private final UserRepository userRepository;
    private final RecipientRequestRepository recipientRequestRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public RecipientRequest createRecipientRequest(RecipientRequestCreateRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User recipient = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        if (!"APPROVED".equalsIgnoreCase(recipient.getStatus())) {
            throw new RuntimeException("Recipient is not approved yet");
        }

        String type = recipient.getUserRoleType();
        if (!("RECIPIENT".equalsIgnoreCase(type) || "BOTH".equalsIgnoreCase(type))) {
            throw new RuntimeException("This user is not registered as a recipient");
        }

        if (request.getOrganRequired() == null || request.getOrganRequired().trim().isEmpty()) {
            throw new RuntimeException("Organ required is mandatory");
        }

        // Save recipient request
        RecipientRequest rr = RecipientRequest.builder()
                .recipient(recipient)
                .organRequired(request.getOrganRequired())
                .status("OPEN")
                .build();

        RecipientRequest saved = recipientRequestRepository.save(rr);

        String organRequired = request.getOrganRequired().trim();

        // Find matching donors
        List<User> donors = userRepository
                .findByStatusAndUserRoleTypeInAndOrgansToDonateContainingIgnoreCase(
                        "APPROVED",
                        List.of("DONOR", "BOTH"),
                        organRequired
                );

        // Apply blood group filter
        donors = donors.stream()
                .filter(d -> d.getBloodGroup() != null)
                .filter(d -> d.getBloodGroup().equalsIgnoreCase(recipient.getBloodGroup()))
                .toList();

        if (donors.isEmpty()) {
            return saved;
        }

        // Sort donors (same hospital first)
        donors.sort((d1, d2) -> {

            boolean d1SameHospital = d1.getHospital() != null && recipient.getHospital() != null
                    && d1.getHospital().getHospitalId().equals(recipient.getHospital().getHospitalId());

            boolean d2SameHospital = d2.getHospital() != null && recipient.getHospital() != null
                    && d2.getHospital().getHospitalId().equals(recipient.getHospital().getHospitalId());

            return Boolean.compare(d2SameHospital, d1SameHospital);
        });

        // Create OrganMatch records
        for (User donor : donors) {

            OrganMatch match = OrganMatch.builder()
                    .donor(donor)
                    .recipient(recipient)
                    .organ(organRequired)
                    .status("PENDING")
                    .build();

            matchRepository.save(match);
        }

        // Notify hospitals (once per hospital)
        java.util.Set<java.util.UUID> notifiedHospitals = new java.util.HashSet<>();

        for (User donor : donors) {

            if (donor.getHospital() == null) continue;

            java.util.UUID hospitalId = donor.getHospital().getHospitalId();

            if (notifiedHospitals.contains(hospitalId)) {
                continue;
            }

            notifiedHospitals.add(hospitalId);

            Notification notify = Notification.builder()
                    .receiverRole("HOSPITAL")
                    .receiverId(hospitalId)
                    .message("Match Found: Recipient request for organ [" + organRequired +
                            "]. Please check dashboard for donor details.")
                    .build();

            notificationRepository.save(notify);
        }

        saved.setStatus("MATCHED");
        recipientRequestRepository.save(saved);

        return saved;
    }
}
