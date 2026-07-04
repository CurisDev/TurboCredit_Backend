package api.turbocredit_backend.iam.interfaces.rest.transform;

import api.turbocredit_backend.iam.domain.model.commands.SignUpCommand;
import api.turbocredit_backend.iam.interfaces.rest.resources.SignUpResource;

public class SignUpCommandFromResourceAssembler {

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(
                resource.email(),
                resource.password(),
                resource.firstName(),
                resource.lastName()
        );
    }
}
