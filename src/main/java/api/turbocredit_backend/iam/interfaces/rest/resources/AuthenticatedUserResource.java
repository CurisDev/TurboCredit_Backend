package api.turbocredit_backend.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(String id, String email, String fullName, String token) {}