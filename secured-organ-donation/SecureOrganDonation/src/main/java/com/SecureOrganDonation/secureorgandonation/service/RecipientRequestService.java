package com.SecureOrganDonation.secureorgandonation.service;

import org.jspecify.annotations.Nullable;

import com.SecureOrganDonation.secureorgandonation.dto.RecipientRequestCreateRequest;
import com.SecureOrganDonation.secureorgandonation.model.RecipientRequest;

public interface RecipientRequestService {
    RecipientRequest createRecipientRequest(RecipientRequestCreateRequest request);

	
}
