package com.SecureOrganDonation.secureorgandonation.repository;



import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SecureOrganDonation.secureorgandonation.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    
    Optional<User> findByNationalId(String nationalId);
    
    long countByHospital_HospitalId(UUID hospitalId);
    
  
    List<User> findByHospital_HospitalIdAndStatus(UUID hospitalId, String status);
    
    List<User> findByStatusAndUserRoleTypeInAndOrgansToDonateContainingIgnoreCase(
            String status,
            List<String> userRoleTypes,
            String organFragment
    );
    
    List<User> findByStatusAndUserRoleTypeAndBloodGroupIgnoreCaseAndOrgansToDonateContainingIgnoreCase(
            String status, String userRoleType, String bloodGroup, String organFragment);
	/*
	 * List<User>
	 * findByStatusAndUserRoleTypeInAndOrgansToDonateContainingIgnoreCase( String
	 * status, List<String> roles, String organFragment);
	 */

    List<User> findByStatusAndUserRoleTypeInAndOrgansToReceiveContainingIgnoreCase(
            String status, List<String> roles, String organFragment);
    
}

