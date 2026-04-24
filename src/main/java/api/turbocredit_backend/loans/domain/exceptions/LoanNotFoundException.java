package api.turbocredit_backend.loans.domain.exceptions;

import api.turbocredit_backend.shared.domain.exceptions.BusinessLogicException;

public class LoanNotFoundException extends BusinessLogicException {
    public LoanNotFoundException(Long loanId) {
        super(String.format("Loan with id '%d' not found", loanId));
    }

    public LoanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}