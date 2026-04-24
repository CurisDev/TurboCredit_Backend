package api.turbocredit_backend.profiles.interfaces.rest.controllers;

import api.turbocredit_backend.profiles.application.internal.commandservices.ProfileCommandServiceImpl;
import api.turbocredit_backend.profiles.application.internal.queryservices.ProfileQueryServiceImpl;
import api.turbocredit_backend.profiles.domain.model.valueobjects.Address;
import api.turbocredit_backend.profiles.domain.model.valueobjects.DocumentId;
import api.turbocredit_backend.profiles.domain.model.valueobjects.PhoneNumber;
import api.turbocredit_backend.profiles.interfaces.rest.resources.CreateProfileRequest;
import api.turbocredit_backend.profiles.interfaces.rest.resources.ProfileResource;
import api.turbocredit_backend.profiles.interfaces.rest.transform.ProfileDtoAssembler;
import api.turbocredit_backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
@Tag(name = "Profiles", description = "Profile management endpoints")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileCommandServiceImpl profileCommandService;
    private final ProfileQueryServiceImpl profileQueryService;

    @PostMapping
    @Operation(summary = "Create a new profile for authenticated user")
    public ResponseEntity<ProfileResource> createProfile(
            @Valid @RequestBody CreateProfileRequest request,
            Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        var profile = profileCommandService.createProfile(
                userId,
                request.getFirstName(),
                request.getLastName(),
                DocumentId.of(request.getDocumentId())
        );

        profile = profileCommandService.updateAddress(
                profile.getId(),
                Address.of(request.getStreet(), request.getCity(),
                        request.getPostalCode(), request.getCountry())
        );

        profile = profileCommandService.updateProfile(
                profile.getId(),
                request.getFirstName(),
                request.getLastName(),
                PhoneNumber.of(request.getPhoneCountryCode(), request.getPhoneNumber())
        );

        profile = profileCommandService.updateFinancialInfo(
                profile.getId(),
                request.getMonthlyIncome(),
                request.getEmploymentStatus()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProfileDtoAssembler.toResource(profile));
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Get profile by ID")
    public ResponseEntity<ProfileResource> getProfile(@PathVariable UUID profileId) {
        var profile = profileQueryService.findById(profileId);
        return profile.map(p -> ResponseEntity.ok(ProfileDtoAssembler.toResource(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get profile by user ID")
    public ResponseEntity<ProfileResource> getProfileByUserId(@PathVariable UUID userId) {
        var profile = profileQueryService.findByUserId(userId);
        return profile.map(p -> ResponseEntity.ok(ProfileDtoAssembler.toResource(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}