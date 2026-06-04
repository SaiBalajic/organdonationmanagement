package com.SecureOrganDonation.secureorgandonation.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.SecureOrganDonation.secureorgandonation.model.OrganMatch;
import com.SecureOrganDonation.secureorgandonation.model.User;
import com.SecureOrganDonation.secureorgandonation.repository.OrganMatchRepository;
import com.SecureOrganDonation.secureorgandonation.repository.UserRepository;
import com.SecureOrganDonation.secureorgandonation.service.OrganMatchService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganMatchServiceImpl implements OrganMatchService {

    private final OrganMatchRepository matchRepository;
    private final UserRepository userRepository;
    
    @Override
    public List<OrganMatch> getMatchesForUser(UUID userId) {

        return matchRepository
                .findByDonor_UserIdOrRecipient_UserId(userId,userId);
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

        // reload and return updated match
        return matchRepository.findById(matchId).orElseThrow();
    }
}