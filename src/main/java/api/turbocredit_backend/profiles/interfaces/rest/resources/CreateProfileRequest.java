package api.turbocredit_backend.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", example = "Juan")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", example = "Pérez")
    private String lastName;

    @NotBlank(message = "Document ID is required")
    @Schema(description = "Document ID (DNI, Passport, etc.)", example = "12345678")
    private String documentId;

    @NotBlank(message = "Phone country code is required")
    @Schema(description = "Phone country code", example = "+51")
    private String phoneCountryCode;

    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number", example = "987654321")
    private String phoneNumber;

    @NotBlank(message = "Street is required")
    @Schema(description = "Street address", example = "Av. Principal 123")
    private String street;

    @NotBlank(message = "City is required")
    @Schema(description = "City", example = "Lima")
    private String city;

    @NotBlank(message = "Postal code is required")
    @Schema(description = "Postal code", example = "15001")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Schema(description = "Country", example = "Perú")
    private String country;

    @NotNull(message = "Monthly income is required")
    @Schema(description = "Monthly income in PEN", example = "3500.00")
    private Double monthlyIncome;

    @NotBlank(message = "Employment status is required")
    @Schema(description = "Employment status", example = "EMPLOYED")
    private String employmentStatus;
}