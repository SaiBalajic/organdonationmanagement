package com.SecureOrganDonation.secureorgandonation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class HospitalRegisterRequest {
	@NotBlank(message = "Hospital name is required")
    @Size(min = 3, max = 100, message = "Hospital name must be between 3 and 100 characters")
    private String hospitalName;

    @NotBlank(message = "License number is required")
    @Size(min = 5, max = 50, message = "License number must be valid")
    private String licenseNumber;

    @NotBlank(message = "Hospital type is required")
    private String hospitalType;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 255, message = "Address must be valid")
    private String address;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;

    @NotBlank(message = "Authorized person name is required")
    @Size(min = 3, max = 100, message = "Authorized person name must be valid")
    private String authorizedPerson;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
