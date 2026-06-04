package com.SecureOrganDonation.secureorgandonation.service;

import java.util.List;
import java.util.UUID;

import com.SecureOrganDonation.secureorgandonation.dto.UserRegisterRequest;
import com.SecureOrganDonation.secureorgandonation.model.Hospital;
import com.SecureOrganDonation.secureorgandonation.model.User;

public interface UserService {
	
	/* List<User> getPendingUser(UUID userId); */

	User registerUser(UserRegisterRequest request);
}
