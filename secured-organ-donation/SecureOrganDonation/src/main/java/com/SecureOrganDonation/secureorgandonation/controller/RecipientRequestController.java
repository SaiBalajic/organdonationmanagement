package com.SecureOrganDonation.secureorgandonation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SecureOrganDonation.secureorgandonation.dto.RecipientRequestCreateRequest;
import com.SecureOrganDonation.secureorgandonation.model.RecipientRequest;
import com.SecureOrganDonation.secureorgandonation.service.RecipientRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recipient-request")
@RequiredArgsConstructor
public class RecipientRequestController {

    private final RecipientRequestService recipientRequestService;

    @PostMapping("/create")
    public ResponseEntity<RecipientRequest> create(@Valid @RequestBody RecipientRequestCreateRequest request) {
        return ResponseEntity.ok(recipientRequestService.createRecipientRequest(request));
    }
}