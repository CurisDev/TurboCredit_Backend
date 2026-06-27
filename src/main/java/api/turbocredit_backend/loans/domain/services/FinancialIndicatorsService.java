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
        BigDecimal cokAnnual = loan.getCok() != null ? loan.getCok() : new BigDecimal("10.0");
        // Convertir COK anual a mensual: cokMonthly = (1 + cokAnnual/100)^(1/12) - 1
        double cokAnnualDouble = cokAnnual.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).doubleValue();
        double cokMonthly = Math.pow(1 + cokAnnualDouble, 1.0 / 12.0) - 1;

        // Flujo inicial (positivo para el cliente): Préstamo
        BigDecimal initialInflow = loan.getLoanAmount();

        double npv = initialInflow.doubleValue();

        for (PaymentScheduleItem item : schedule) {
            double totalInstallment = item.getTotalInstallment().doubleValue();
            // Descontar flujo de caja negativo (pago)
            npv -= totalInstallment / Math.pow(1 + cokMonthly, item.getPeriod());
        }

        return BigDecimal.valueOf(npv).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la TIR (Tasa Interna de Retorno) de los flujos netos usando Newton-Raphson
     */
    public BigDecimal calculateIRR(VehicleCredit loan, List<PaymentScheduleItem> schedule) {
        double initialInflow = loan.getLoanAmount().doubleValue();

        double[] flows = new double[schedule.size() + 1];
        flows[0] = initialInflow;

        for (int i = 0; i < schedule.size(); i++) {
            flows[i + 1] = -schedule.get(i).getTotalInstallment().doubleValue();
        }

        double irr = 0.01; // Tasa inicial guess: 1% mensual
        double tolerance = 0.0000001;
        int maxIterations = 1000;

        for (int iter = 0; iter < maxIterations; iter++) {
            double npv = 0.0;
            double npvDerivative = 0.0;

            for (int t = 0; t < flows.length; t++) {
                double factor = Math.pow(1 + irr, t);
                npv += flows[t] / factor;
                if (t > 0) {
                    npvDerivative -= flows[t] * t / (factor * (1 + irr));
                }
            }

            if (Math.abs(npv) < tolerance) {
                return BigDecimal.valueOf(irr).setScale(10, RoundingMode.HALF_UP);
            }

            if (npvDerivative == 0) {
                break;
            }

            double nextIrr = irr - npv / npvDerivative;
            if (Double.isNaN(nextIrr) || Double.isInfinite(nextIrr) || nextIrr < -1 || nextIrr > 10) {
                break;
            }

            if (Math.abs(nextIrr - irr) < tolerance) {
                return BigDecimal.valueOf(nextIrr).setScale(10, RoundingMode.HALF_UP);
            }

            irr = nextIrr;
        }

        return BigDecimal.valueOf(irr).setScale(10, RoundingMode.HALF_UP);
    }

    /**
     * Calcula TCEA (Tasa de Costo Efectivo Anual) a partir de la TIR mensual
     * TCEA = (1 + TIR_mensual)^12 - 1
     */
    public BigDecimal calculateTCEA(BigDecimal monthlyIrr) {
        double irr = monthlyIrr.doubleValue();
        double tceaDouble = Math.pow(1 + irr, 12) - 1;

        return BigDecimal.valueOf(tceaDouble * 100).setScale(6, RoundingMode.HALF_UP);
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