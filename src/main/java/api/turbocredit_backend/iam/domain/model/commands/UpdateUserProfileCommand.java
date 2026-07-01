package api.turbocredit_backend.iam.domain.model.commands;

import java.util.UUID;

public record UpdateUserProfileCommand(UUID userId, String firstName, String lastName, String profileImageUrl) {}
