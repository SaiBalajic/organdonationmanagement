package com.SecureOrganDonation.secureorgandonation.service;

import java.util.List;
import java.util.UUID;

import com.SecureOrganDonation.secureorgandonation.model.Hospital;

public interface AdminService {
	
	List<Hospital> getPendingHospitals();
	List<Hospital> getAllHospitals();

    Hospital approveHospital(UUID hospitalId);

    Hospital rejectHospital(UUID hospitalId);

    void removeUser(UUID userId);

    void removeHospital(UUID hospitalId);
	
}
