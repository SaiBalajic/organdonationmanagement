package com.SecureOrganDonation.secureorgandonation.service;


import java.util.List;
import java.util.UUID;
import com.SecureOrganDonation.secureorgandonation.model.OrganMatch;

public interface MatchingService {
    void runMatchingForUser(UUID userId); // run matching triggered by a single user approval
    void runFullMatching(); // optional: one-time batch to populate legacy data
    OrganMatch acceptMatch(UUID matchId, UUID actingUserId);
    List<OrganMatch> getMatchesForUser(UUID userId);
	
	
}
