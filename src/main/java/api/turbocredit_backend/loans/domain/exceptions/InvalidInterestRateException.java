package api.turbocredit_backend.loans.domain.exceptions;

import api.turbocredit_backend.shared.domain.exceptions.BusinessLogicException;

import java.math.BigDecimal;

public class InvalidInterestRateException extends BusinessLogicException {
    public InvalidInterestRateException(BigDecimal rate) {
        super(String.format("Invalid interest rate: %s. Must be between 0 and 100", rate));
    }

    public InvalidInterestRateException(String message) {
        super(message);
    }

    public InvalidInterestRateException(String message, Throwable cause) {
        super(message, cause);
    }
}