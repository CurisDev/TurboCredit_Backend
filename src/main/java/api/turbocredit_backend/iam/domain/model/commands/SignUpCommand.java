package api.turbocredit_backend.iam.domain.model.commands;

import api.turbocredit_backend.iam.domain.model.valueobjects.Role;
import java.util.List;

public record SignUpCommand(String email, String password, String fullName, List<Role> roles) {}