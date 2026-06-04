package com.SecureOrganDonation.secureorgandonation.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganRequestCreateRequest {
    
	@NotNull(message = "Target hospital ID is required")
    private UUID targetHospitalId;

    @NotBlank(message = "Organ name is required")
    @Size(min = 3, max = 50, message = "Organ name must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Organ name must contain only letters and spaces")
    private String organ;
}