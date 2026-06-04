package com.SecureOrganDonation.secureorgandonation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
public class UserRegisterRequest {
	@NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Full name must contain only letters and spaces")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$",
            message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$",
            message = "Invalid blood group format (e.g., A+, O-)")
    private String bloodGroup;

    @NotBlank(message = "National ID is required")
    @Size(min = 6, max = 20, message = "National ID must be valid")
    private String nationalId;

    @NotBlank(message = "User role type is required")
    @Pattern(regexp = "^(DONOR|RECIPIENT|BOTH)$",
            message = "Role must be DONOR, RECIPIENT, or BOTH")
    private String userRoleType;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Phone number must be 10 digits")
    private String phone;

    @Size(max = 200, message = "Organs list too long")
    private String organsToDonate;

    @NotBlank(message = "Emergency contact is required")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Emergency contact must be 10 digits")
    private String emergencyContact;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Consent is required")
    private Boolean consent;

    @NotBlank(message = "Hospital license number is required")
    private String hospitalLicenseNumber;

    @Size(max = 200, message = "Organs list too long")
	public String organsToReceive;

	
}
