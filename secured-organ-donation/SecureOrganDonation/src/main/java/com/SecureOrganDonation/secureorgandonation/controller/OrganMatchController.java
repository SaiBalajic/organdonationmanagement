package com.SecureOrganDonation.secureorgandonation.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SecureOrganDonation.secureorgandonation.model.OrganMatch;
import com.SecureOrganDonation.secureorgandonation.service.MatchingService;
import com.SecureOrganDonation.secureorgandonation.service.OrganMatchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class OrganMatchController {

    private final MatchingService matchService;

    @GetMapping("/my")
    public ResponseEntity<List<OrganMatch>> getMyMatches(Authentication auth){

        UUID userId = UUID.fromString(auth.getCredentials().toString());

        return ResponseEntity.ok(
                matchService.getMatchesForUser(userId)
        );
    }


    @PutMapping("/{matchId}/accept")
    public ResponseEntity<?> acceptMatch(@PathVariable UUID matchId, Authentication auth){

        UUID userId = UUID.fromString(auth.getCredentials().toString());

        try {

            OrganMatch accepted = matchService.acceptMatch(matchId, userId);

            return ResponseEntity.ok(accepted);

        } catch (RuntimeException re) {

            return ResponseEntity.status(400).body(
                Map.of("message", re.getMessage())
            );

        } catch (Exception e) {

            return ResponseEntity.status(500).body(
                Map.of("message","error","detail", e.getMessage())
            );
        }
    }
}