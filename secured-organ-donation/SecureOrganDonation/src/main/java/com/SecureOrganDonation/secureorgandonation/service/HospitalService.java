package com.SecureOrganDonation.secureorgandonation.service;

import java.util.List;
import java.util.UUID;

import com.SecureOrganDonation.secureorgandonation.dto.HospitalRegisterRequest;
import com.SecureOrganDonation.secureorgandonation.model.Hospital;
import com.SecureOrganDonation.secureorgandonation.model.User;

public interface HospitalService 
{
	Hospital registerHospital(HospitalRegisterRequest request);
	
	List<User> getPendingUsers(UUID hospitalId);

    User approveUser(UUID hospitalId, UUID userId);

    User rejectUser(UUID hospitalId, UUID userId);
}
