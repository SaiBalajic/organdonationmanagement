package com.SecureOrganDonation.secureorgandonation.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SecureOrganDonation.secureorgandonation.model.OrganMatch;
import com.SecureOrganDonation.secureorgandonation.model.User;

public interface OrganMatchRepository extends JpaRepository<OrganMatch, UUID>{
	
	boolean existsByDonorAndRecipient(User donor, User recipient);
    List<OrganMatch> findByDonor_UserIdOrRecipient_UserId(UUID donorId, UUID recipientId);
    List<OrganMatch> findByRecipient_UserId(UUID recipientId);
    List<OrganMatch> findByRecipient_UserIdOrDonor_UserId(UUID recipientId, UUID donorId);
    

    
 // Try to accept the match only if its status is PENDING. Returns number of rows updated (1 = success, 0 = already accepted/rejected)
    @Modifying
    @Query("UPDATE OrganMatch m SET m.status = 'ACCEPTED', m.acceptedBy = :acceptedBy WHERE m.matchId = :matchId AND m.status = 'PENDING'")
    int acceptIfPending(@Param("matchId") UUID matchId, @Param("acceptedBy") String acceptedBy);

    // Reject other pending matches for the same recipient (except the accepted one)
    @Modifying
    @Query("UPDATE OrganMatch m SET m.status = 'REJECTED' WHERE m.recipient.userId = :recipientId AND m.matchId <> :acceptedMatchId AND m.status = 'PENDING'")
    int rejectOtherPending(@Param("recipientId") UUID recipientId, @Param("acceptedMatchId") UUID acceptedMatchId);
    
    @Modifying
    @Query("""
    UPDATE OrganMatch m
    SET m.status = 'REJECTED'
    WHERE m.donor.userId = :donorId
    AND m.matchId <> :acceptedMatchId
    AND m.status = 'PENDING'
    """)
    int rejectOtherPendingForDonor(UUID donorId, UUID acceptedMatchId);
	
    
}