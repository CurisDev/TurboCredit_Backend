package api.turbocredit_backend.iam.interfaces.rest.transform;

import api.turbocredit_backend.iam.domain.model.aggregates.User;
import api.turbocredit_backend.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {

    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(
                user.getId().toString(),
                user.getEmail(),
                user.getFullName(),
                token
        );
    }
}