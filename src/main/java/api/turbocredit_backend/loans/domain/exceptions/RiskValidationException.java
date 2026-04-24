package api.turbocredit_backend.loans.domain.exceptions;

import api.turbocredit_backend.shared.domain.exceptions.BusinessLogicException;

public class RiskValidationException extends BusinessLogicException {
    public RiskValidationException(String message) {
        super(message);
    }

    public RiskValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}