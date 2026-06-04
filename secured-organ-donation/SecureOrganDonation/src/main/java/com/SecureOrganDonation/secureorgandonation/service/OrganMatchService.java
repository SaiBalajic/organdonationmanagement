package com.SecureOrganDonation.secureorgandonation.service;

import java.util.List;
import java.util.UUID;

import com.SecureOrganDonation.secureorgandonation.model.OrganMatch;

public interface OrganMatchService {

    List<OrganMatch> getMatchesForUser(UUID userId);

    OrganMatch acceptMatch(UUID matchId, UUID userId);

}
