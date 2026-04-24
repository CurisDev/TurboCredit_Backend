package api.turbocredit_backend.loans.domain.services;

import api.turbocredit_backend.loans.domain.exceptions.RiskValidationException;
import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Servicio para validar el riesgo del crédito
 */
@Service  // ✅ AGREGADO: Anotación Service para que sea Bean
public class RiskValidationService {

    private static final BigDecimal LTV_LIMIT = new BigDecimal("0.90"); // 90%
    private static final BigDecimal MAX_DEBT_RATIO = new BigDecimal("0.40"); // 40%
    private static final BigDecimal MIN_DOWN_PAYMENT_PERCENT = new BigDecimal("0.10"); // 10%

    /**
     * Valida si el crédito es viable según criterios de riesgo
     */
    public void validateLoan(VehicleCredit loan, BigDecimal monthlyIncome) {
        validateLTV(loan);
        validateDebtRatio(loan, monthlyIncome);
        validateDownPayment(loan);
    }

    /**
     * LTV (Loan-to-Value): Relación entre préstamo y valor del vehículo
     * No debe exceder 90%
     */
    private void validateLTV(VehicleCredit loan) {
        BigDecimal ltv = loan.getLoanAmount()
                .divide(loan.getVehiclePrice(), 4, java.math.RoundingMode.HALF_UP);

        if (ltv.compareTo(LTV_LIMIT) > 0) {
            throw new RiskValidationException(
                    String.format("LTV (%.2f%%) exceeds maximum allowed (90%%)",
                            ltv.multiply(new BigDecimal(100)))
            );
        }
    }

    /**
     * Relación de endeudamiento: Cuota mensual / Ingreso mensual
     * No debe exceder 40%
     */
    private void validateDebtRatio(VehicleCredit loan, BigDecimal monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RiskValidationException("Monthly income must be greater than 0");
        }

        BigDecimal debtRatio = loan.getFixedInstallment()
                .divide(monthlyIncome, 4, java.math.RoundingMode.HALF_UP);

        if (debtRatio.compareTo(MAX_DEBT_RATIO) > 0) {
            throw new RiskValidationException(
                    String.format("Debt ratio (%.2f%%) exceeds maximum allowed (40%%)",
                            debtRatio.multiply(new BigDecimal(100)))
            );
        }
    }

    /**
     * Cuota inicial mínima: 10% del precio del vehículo
     */
    private void validateDownPayment(VehicleCredit loan) {
        BigDecimal minDownPayment = loan.getVehiclePrice()
                .multiply(MIN_DOWN_PAYMENT_PERCENT);

        if (loan.getDownPayment().compareTo(minDownPayment) < 0) {
            throw new RiskValidationException(
                    String.format("Down payment (%.2f) must be at least 10%% of vehicle price (%.2f)",
                            loan.getDownPayment(), minDownPayment)
            );
        }
    }
}