package api.turbocredit_backend.iam.interfaces.rest.transform;

import api.turbocredit_backend.iam.domain.model.commands.SignInCommand;
import api.turbocredit_backend.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {

    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.email(), resource.password());
    }
}