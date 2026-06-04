package com.SecureOrganDonation.secureorgandonation.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.SecureOrganDonation.secureorgandonation.model.User;
import com.SecureOrganDonation.secureorgandonation.model.OrganMatch;
import com.SecureOrganDonation.secureorgandonation.repository.UserRepository;
import com.SecureOrganDonation.secureorgandonation.repository.OrganMatchRepository;
import com.SecureOrganDonation.secureorgandonation.service.MatchingService;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final UserRepository userRepository;
    private final OrganMatchRepository matchRepository;

    /**
     * Run matching for a single user who was just approved.
     * If the user is RECIPIENT, search donors; if DONOR, search recipients.
     */
    @Override
    public void runMatchingForUser(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !"APPROVED".equalsIgnoreCase(user.getStatus())) return;

        // recipient approved: find donors that match
        if ("RECIPIENT".equalsIgnoreCase(user.getUserRoleType()) || "BOTH".equalsIgnoreCase(user.getUserRoleType())) {
            if (user.getOrgansToReceive() == null) return;
            String organ = user.getOrgansToReceive().trim();
            List<User> donors = userRepository.findByStatusAndUserRoleTypeInAndOrgansToDonateContainingIgnoreCase(
                    "APPROVED", List.of("DONOR","BOTH"), organ
            );

            donors.stream()
                  .filter(d -> d.getUserId() != null && !d.getUserId().equals(user.getUserId()))
                  .filter(d -> d.getBloodGroup() != null && d.getBloodGroup().equalsIgnoreCase(user.getBloodGroup()))
                  .forEach(donor -> {
                      if (!matchRepository.existsByDonorAndRecipient(donor, user)) {
                          OrganMatch m = OrganMatch.builder()
                                  .donor(donor)
                                  .recipient(user)
                                  .organ(organ)
                                  .status("PENDING")
                                  .build();
                          matchRepository.save(m);
                      }
                  });
        }

        // donor approved: find recipients
        if ("DONOR".equalsIgnoreCase(user.getUserRoleType()) || "BOTH".equalsIgnoreCase(user.getUserRoleType())) {
            if (user.getOrgansToDonate() == null) return;
            String donorOrgans = user.getOrgansToDonate().trim();
            List<User> recipients = userRepository.findByStatusAndUserRoleTypeInAndOrgansToDonateContainingIgnoreCase(
                    "APPROVED", List.of("RECIPIENT","BOTH"), "" /* can't pass multiple organ fragments easily, so fallback to scanning recipients */);

            // fallback: fetch all recipients and filter
            List<User> recipList = userRepository.findAll().stream()
                    .filter(r -> ("RECIPIENT".equalsIgnoreCase(r.getUserRoleType()) || "BOTH".equalsIgnoreCase(r.getUserRoleType())))
                    .filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus()))
                    .filter(r -> r.getOrgansToReceive() != null)
                    .filter(r -> donorOrgans.toLowerCase().contains(r.getOrgansToReceive().toLowerCase()))
                    .filter(r -> r.getBloodGroup() != null && r.getBloodGroup().equalsIgnoreCase(user.getBloodGroup()))
                    .toList();

            for (User recipient : recipList) {
                if (!matchRepository.existsByDonorAndRecipient(user, recipient)) {
                    OrganMatch m = OrganMatch.builder()
                            .donor(user)
                            .recipient(recipient)
                            .organ(recipient.getOrgansToReceive())
                            .status("PENDING")
                            .build();
                    matchRepository.save(m);
                }
            }
        }
    }

    /**
     * One-time full run to populate matches for existing rows.
     */
    @Override
    public void runFullMatching() {
        List<User> donors = userRepository.findAll().stream()
                .filter(u -> "APPROVED".equalsIgnoreCase(u.getStatus()))
                .filter(u -> u.getOrgansToDonate() != null)
                .filter(u -> "DONOR".equalsIgnoreCase(u.getUserRoleType()) || "BOTH".equalsIgnoreCase(u.getUserRoleType()))
                .toList();

        List<User> recipients = userRepository.findAll().stream()
                .filter(u -> "APPROVED".equalsIgnoreCase(u.getStatus()))
                .filter(u -> u.getOrgansToReceive() != null)
                .filter(u -> "RECIPIENT".equalsIgnoreCase(u.getUserRoleType()) || "BOTH".equalsIgnoreCase(u.getUserRoleType()))
                .toList();

        for (User donor : donors) {
            for (User recipient : recipients) {
                if (donor.getUserId().equals(recipient.getUserId())) continue;

                if (!donor.getBloodGroup().equalsIgnoreCase(recipient.getBloodGroup())) continue;

                if (!donor.getOrgansToDonate().toLowerCase().contains(recipient.getOrgansToReceive().toLowerCase()))
                    continue;

                if (!matchRepository.existsByDonorAndRecipient(donor, recipient)) {
                    OrganMatch m = OrganMatch.builder()
                            .donor(donor)
                            .recipient(recipient)
                            .organ(recipient.getOrgansToReceive())
                            .status("PENDING")
                            .build();
                    matchRepository.save(m);
                }
            }
        }
    }

    
    @Override
    @Transactional
    public OrganMatch acceptMatch(UUID matchId, UUID actingUserId) {
        // load match
        OrganMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // confirm actor is the recipient
        User recipient = match.getRecipient();
        if (recipient == null || recipient.getUserId() == null) {
            throw new RuntimeException("Invalid match recipient");
        }
        if (!recipient.getUserId().equals(actingUserId)) {
            throw new RuntimeException("Only the recipient can accept this match");
        }

        // Attempt atomic accept: if another user already accepted, this will return 0
        String acceptedBy = "";
        try {
            // prefer storing acceptor name (could also store id)
            acceptedBy = userRepository.findById(actingUserId).map(User::getFullName).orElse("recipient");
        } catch (Exception e) {
            acceptedBy = "recipient";
        }

        int updated = matchRepository.acceptIfPending(matchId, acceptedBy);
        if (updated == 0) {
            // someone else accepted or this match no longer pending
            throw new RuntimeException("Match already accepted or not available");
        }

        // Reject other pending matches for this recipient
        matchRepository.rejectOtherPending(recipient.getUserId(), matchId);
        
        matchRepository.rejectOtherPendingForDonor(match.getDonor().getUserId(), matchId);

        // reload and return updated match
        return matchRepository.findById(matchId).orElseThrow();
    }
    
    
    
    @Override
    @Transactional(readOnly = true)
    public List<OrganMatch> getMatchesForUser(UUID userId) {
        if (userId == null) return List.of();

        // Simple approach: load matches and filter by donor or recipient id
        return matchRepository.findAll().stream()
                .filter(m -> (m.getDonor() != null && userId.equals(m.getDonor().getUserId()))
                          || (m.getRecipient() != null && userId.equals(m.getRecipient().getUserId())))
                .collect(Collectors.toList());
    }
    
    
    
    
    
}

	
