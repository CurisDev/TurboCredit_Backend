package api.turbocredit_backend.iam.domain.model.commands;

public record SignUpCommand(String email, String password, String firstName, String lastName) {}
