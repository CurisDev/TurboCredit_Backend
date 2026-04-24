package api.turbocredit_backend.loans.domain.exceptions;

import api.turbocredit_backend.shared.domain.exceptions.BusinessLogicException;

import java.math.BigDecimal;

public class InvalidLoanAmountException extends BusinessLogicException {
    public InvalidLoanAmountException(BigDecimal amount) {
        super(String.format("Invalid loan amount: %s. Must be greater than 0", amount));
    }

    public InvalidLoanAmountException(String message) {
        super(message);
    }

    public InvalidLoanAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}