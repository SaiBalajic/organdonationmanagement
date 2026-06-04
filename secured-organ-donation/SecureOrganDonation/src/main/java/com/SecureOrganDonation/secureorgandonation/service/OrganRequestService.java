package com.SecureOrganDonation.secureorgandonation.service;

import com.SecureOrganDonation.secureorgandonation.dto.OrganRequestCreateRequest;
import com.SecureOrganDonation.secureorgandonation.model.OrganRequest;

public interface OrganRequestService {
    OrganRequest createRequest(OrganRequestCreateRequest request);
}
