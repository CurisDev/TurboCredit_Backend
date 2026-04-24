package api.turbocredit_backend.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResource {

    @Schema(description = "Profile unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Associated user ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "First name", example = "Juan")
    private String firstName;

    @Schema(description = "Last name", example = "Pérez")
    private String lastName;

    @Schema(description = "Document ID (DNI, Passport, etc.)", example = "12345678")
    private String documentId;

    @Schema(description = "Phone country code", example = "+51")
    private String phoneCountryCode;

    @Schema(description = "Phone number", example = "987654321")
    private String phoneNumber;

    @Schema(description = "Street address", example = "Av. Principal 123")
    private String street;

    @Schema(description = "City", example = "Lima")
    private String city;

    @Schema(description = "Postal code", example = "15001")
    private String postalCode;

    @Schema(description = "Country", example = "Perú")
    private String country;

    @Schema(description = "Monthly income in PEN", example = "3500.00")
    private Double monthlyIncome;

    @Schema(description = "Employment status", example = "EMPLOYED")
    private String employmentStatus;
}