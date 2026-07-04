package api.turbocredit_backend.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(String id, String email, String firstName, String lastName, String token) {}
