package com.SecureOrganDonation.secureorgandonation.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.dto.OrganRequestCreateRequest;
import com.SecureOrganDonation.secureorgandonation.model.Hospital;
import com.SecureOrganDonation.secureorgandonation.model.Notification;
import com.SecureOrganDonation.secureorgandonation.model.OrganRequest;
import com.SecureOrganDonation.secureorgandonation.model.User;
import com.SecureOrganDonation.secureorgandonation.repository.HospitalRepository;
import com.SecureOrganDonation.secureorgandonation.repository.NotificationRepository;
import com.SecureOrganDonation.secureorgandonation.repository.OrganRequestRepository;
import com.SecureOrganDonation.secureorgandonation.repository.UserRepository;
import com.SecureOrganDonation.secureorgandonation.service.OrganRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganRequestServiceImpl implements OrganRequestService {

    private final OrganRequestRepository organRequestRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    
    @Override
    public OrganRequest createRequest(OrganRequestCreateRequest request) {

    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String email = auth.getName();

    	User donor = userRepository.findByEmail(email)
    	        .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        if (!"APPROVED".equalsIgnoreCase(donor.getStatus())) {
            throw new RuntimeException("Donor is not approved yet");
        }

        String type = donor.getUserRoleType();
        if (!( "DONOR".equalsIgnoreCase(type) || "BOTH".equalsIgnoreCase(type) )) {
            throw new RuntimeException("This user is not registered as a donor");
        }

        Hospital sourceHospital = donor.getHospital();
        if (sourceHospital == null) {
            throw new RuntimeException("Donor hospital not found");
        }

        Hospital targetHospital = hospitalRepository.findById(request.getTargetHospitalId())
                .orElseThrow(() -> new RuntimeException("Target hospital not found"));

        OrganRequest organRequest = OrganRequest.builder()
                .donor(donor)
                .sourceHospital(sourceHospital)
                .targetHospital(targetHospital)
                .organ(request.getOrgan())
                .status("INITIATED")
                .build();

        OrganRequest saved = organRequestRepository.save(organRequest);

        // Notifications
        if (sourceHospital.getHospitalId().equals(targetHospital.getHospitalId())) {

            Notification n1 = Notification.builder()
                    .receiverRole("HOSPITAL")
                    .receiverId(sourceHospital.getHospitalId())
                    .message("New organ donation request: " + request.getOrgan() +
                            " (Donor: " + donor.getFullName() + ")")
                    .build();

            notificationRepository.save(n1);

        } else {

            Notification sourceNotify = Notification.builder()
                    .receiverRole("HOSPITAL")
                    .receiverId(sourceHospital.getHospitalId())
                    .message("Donation request initiated for organ: " + request.getOrgan() +
                            " to Hospital: " + targetHospital.getHospitalName())
                    .build();

            Notification targetNotify = Notification.builder()
                    .receiverRole("HOSPITAL")
                    .receiverId(targetHospital.getHospitalId())
                    .message("Incoming donation request for organ: " + request.getOrgan() +
                            " from Hospital: " + sourceHospital.getHospitalName())
                    .build();

            notificationRepository.save(sourceNotify);
            notificationRepository.save(targetNotify);
        }

        return saved;
    }

}
