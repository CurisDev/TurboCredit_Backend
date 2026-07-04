package api.turbocredit_backend.iam.interfaces.rest;

import api.turbocredit_backend.iam.domain.model.commands.UpdateUserProfileCommand;
import api.turbocredit_backend.iam.domain.model.queries.GetUserByIdQuery;
import api.turbocredit_backend.iam.domain.services.UserCommandService;
import api.turbocredit_backend.iam.domain.services.UserQueryService;
import api.turbocredit_backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import api.turbocredit_backend.iam.interfaces.rest.resources.UpdateProfileResource;
import api.turbocredit_backend.iam.interfaces.rest.resources.UserResource;
import api.turbocredit_backend.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UsersController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieve the authenticated user's profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")})
    public ResponseEntity<UserResource> getCurrentUser(Authentication authentication) {
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var user = userQueryService.handle(new GetUserByIdQuery(userDetails.getId()));
        return user.map(u -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile",
            description = "Update the authenticated user's first name, last name and profile image URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")})
    public ResponseEntity<UserResource> updateCurrentUser(
            @Valid @RequestBody UpdateProfileResource resource,
            Authentication authentication) {
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var command = new UpdateUserProfileCommand(
                userDetails.getId(),
                resource.firstName(),
                resource.lastName(),
                resource.profileImageUrl()
        );
        var user = userCommandService.handle(command);
        return user.map(u -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")})
    public ResponseEntity<UserResource> getUserById(@PathVariable UUID userId) {
        var user = userQueryService.handle(new GetUserByIdQuery(userId));
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user.get()));
    }
}
