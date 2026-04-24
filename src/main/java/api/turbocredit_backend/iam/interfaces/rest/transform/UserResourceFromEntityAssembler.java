package api.turbocredit_backend.iam.interfaces.rest.transform;

import api.turbocredit_backend.iam.domain.model.aggregates.User;
import api.turbocredit_backend.iam.domain.model.valueobjects.Role;
import api.turbocredit_backend.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {

    public static UserResource toResourceFromEntity(User user) {
        var roles = user.getRoles().stream()
                .map(Role::getStringName)
                .toList();
        return new UserResource(user.getId(), user.getEmail(), user.getFullName(), roles);
    }
}