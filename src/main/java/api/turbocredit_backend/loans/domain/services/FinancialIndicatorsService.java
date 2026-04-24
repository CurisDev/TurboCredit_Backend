package api.turbocredit_backend.loans.domain.services;

import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.entities.PaymentScheduleItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Servicio para calcular indicadores financieros: VAN, TIR, TCEA
 * Requeridos por la norma de transparencia de la SBS Perú
 */
@Service  // ✅ AGREGADO: Anotación Service para que sea Bean
public class FinancialIndicatorsService {

    /**
     * Calcula el VAN (Valor Presente Neto)
     * VAN = -Inversión inicial + Σ(Flujo / (1 + r)^t)
     * En este caso: VAN = -Principal + Σ(Cuotas / (1 + TEM)^periodo)
     */
    public BigDecimal calculateNPV(VehicleCredit loan, List<PaymentScheduleItem> schedule) {
        BigDecimal monthlyRate = loan.getPeriodicRate().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
        BigDecimal npv = loan.getPrincipalFinanced().negate(); // Flujo inicial negativo

        for (PaymentScheduleItem item : schedule) {
            BigDecimal discountFactor = BigDecimal.ONE.add(monthlyRate)
                    .pow(item.getPeriod());
            BigDecimal presentValue = item.getInstallment()
                    .divide(discountFactor, 10, RoundingMode.HALF_UP);
            npv = npv.add(presentValue);
        }

        return npv.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la TIR (Tasa Interna de Retorno) usando Newton-Raphson
     * TIR es la tasa que hace VAN = 0
     */
    public BigDecimal calculateIRR(VehicleCredit loan, List<PaymentScheduleItem> schedule) {
        BigDecimal irr = new BigDecimal("0.01"); // Tasa inicial: 1%
        BigDecimal tolerance = new BigDecimal("0.00001");
        int maxIterations = 100;

        for (int i = 0; i < maxIterations; i++) {
            BigDecimal npv = calculateNPVAtRate(loan.getPrincipalFinanced(), schedule, irr);
            BigDecimal npvDerivative = calculateNPVDerivative(schedule, irr);

            if (npvDerivative.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }

            BigDecimal nextIrr = irr.subtract(npv.divide(npvDerivative, 10, RoundingMode.HALF_UP));

            if (nextIrr.subtract(irr).abs().compareTo(tolerance) < 0) {
                return nextIrr.multiply(new BigDecimal(100))
                        .setScale(6, RoundingMode.HALF_UP);
            }

            irr = nextIrr;
        }

        return irr.multiply(new BigDecimal(100))
                .setScale(6, RoundingMode.HALF_UP);
    }

    /**
     * Calcula TCEA (Tasa de Costo Efectivo Anual)
     * TCEA = (1 + TEM)^12 - 1
     * Donde TEM es obtenida de la TIR
     */
    public BigDecimal calculateTCEA(BigDecimal monthlyRate) {
        BigDecimal tem = monthlyRate.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusTem = BigDecimal.ONE.add(tem);

        // (1 + TEM)^12
        BigDecimal annualFactor = onePlusTem.pow(12);

        // TCEA = ((1 + TEM)^12 - 1) * 100
        BigDecimal tcea = annualFactor.subtract(BigDecimal.ONE)
                .multiply(new BigDecimal(100))
                .setScale(6, RoundingMode.HALF_UP);

        return tcea;
    }

    private BigDecimal calculateNPVAtRate(BigDecimal principal, List<PaymentScheduleItem> schedule, BigDecimal rate) {
        BigDecimal npv = principal.negate();

        for (PaymentScheduleItem item : schedule) {
            BigDecimal discountFactor = BigDecimal.ONE.add(rate).pow(item.getPeriod());
            BigDecimal presentValue = item.getInstallment()
                    .divide(discountFactor, 10, RoundingMode.HALF_UP);
            npv = npv.add(presentValue);
        }

        return npv;
    }

    private BigDecimal calculateNPVDerivative(List<PaymentScheduleItem> schedule, BigDecimal rate) {
        BigDecimal derivative = BigDecimal.ZERO;

        for (PaymentScheduleItem item : schedule) {
            BigDecimal factor = BigDecimal.ONE.add(rate).pow(item.getPeriod() + 1);
            BigDecimal term = item.getInstallment()
                    .multiply(new BigDecimal(-item.getPeriod()))
                    .divide(factor, 10, RoundingMode.HALF_UP);
            derivative = derivative.add(term);
        }

        return derivative;
    }
}