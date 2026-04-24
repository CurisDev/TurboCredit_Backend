package api.turbocredit_backend.profiles.domain.exceptions;

import api.turbocredit_backend.shared.domain.exceptions.BusinessLogicException;

public class InvalidDocumentIdException extends BusinessLogicException {
    public InvalidDocumentIdException(String documentId) {
        super(String.format("Invalid document ID: '%s'. Must be 8-12 alphanumeric characters", documentId));
    }

    public InvalidDocumentIdException(String message, Throwable cause) {
        super(message, cause);
    }
}