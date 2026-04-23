package api.turbocredit_backend.iam.domain.services;

import api.turbocredit_backend.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}