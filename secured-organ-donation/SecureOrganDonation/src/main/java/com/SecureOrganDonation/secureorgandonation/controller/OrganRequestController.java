package com.SecureOrganDonation.secureorgandonation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SecureOrganDonation.secureorgandonation.dto.OrganRequestCreateRequest;
import com.SecureOrganDonation.secureorgandonation.model.OrganRequest;
import com.SecureOrganDonation.secureorgandonation.service.OrganRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/organ-request")
@RequiredArgsConstructor
public class OrganRequestController {

    private final OrganRequestService organRequestService;

    @PostMapping("/create")
    public ResponseEntity<OrganRequest> create(@Valid @RequestBody OrganRequestCreateRequest request) {
        return ResponseEntity.ok(organRequestService.createRequest(request));
    }
}
