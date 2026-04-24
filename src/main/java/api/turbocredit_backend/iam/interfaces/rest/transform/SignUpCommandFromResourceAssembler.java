package api.turbocredit_backend.iam.interfaces.rest.transform;

import api.turbocredit_backend.iam.domain.model.commands.SignUpCommand;
import api.turbocredit_backend.iam.domain.model.valueobjects.Role;
import api.turbocredit_backend.iam.interfaces.rest.resources.SignUpResource;

import java.util.List;

public class SignUpCommandFromResourceAssembler {

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        List<Role> roles = resource.roles() != null
                ? resource.roles().stream().map(Role::toRoleFromName).toList()
                : List.of(Role.getDefaultRole());

        return new SignUpCommand(
                resource.email(),
                resource.password(),
                resource.fullName(),
                roles
        );
    }
}