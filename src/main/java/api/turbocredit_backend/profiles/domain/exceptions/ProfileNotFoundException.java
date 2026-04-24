package api.turbocredit_backend.profiles.domain.exceptions;

import api.turbocredit_backend.shared.domain.exceptions.BusinessLogicException;

public class ProfileNotFoundException extends BusinessLogicException {
    public ProfileNotFoundException(String profileId) {
        super(String.format("Profile with id '%s' not found", profileId));
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}