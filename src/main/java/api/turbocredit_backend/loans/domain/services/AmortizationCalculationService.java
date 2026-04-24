package api.turbocredit_backend.loans.domain.services;

import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.entities.PaymentScheduleItem;
import api.turbocredit_backend.loans.domain.model.valueobjects.GracePeriodType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Servicio de dominio para calcular amortización usando método francés vencido ordinario
 * Fórmula: Cuota fija = P * [r(1+r)^n] / [(1+r)^n - 1]
 */
@Service
public class AmortizationCalculationService {

    /**
     * Genera el cronograma de pagos completo
     */
    public List<PaymentScheduleItem> generatePaymentSchedule(VehicleCredit loan) {
        List<PaymentScheduleItem> schedule = new ArrayList<>();

        BigDecimal monthlyRate = loan.getPeriodicRate().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
        BigDecimal remainingBalance = loan.getPrincipalFinanced();
        BigDecimal fixedInstallment = loan.getFixedInstallment();

        int gracePeriods = loan.getGracePeriodMonths();
        int totalPeriods = loan.getTermMonths();
        GracePeriodType gracePeriodType = loan.getGracePeriodType();

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        // Generar períodos de gracia
        for (int period = 1; period <= gracePeriods; period++) {
            calendar.add(Calendar.MONTH, 1);
            Date dueDate = (Date) calendar.getTime().clone();

            BigDecimal interestDueGrace = BigDecimal.ZERO;
            BigDecimal amortizationDueGrace = BigDecimal.ZERO;
            BigDecimal installmentDueGrace = fixedInstallment;

            if (gracePeriodType.isPartial()) {
                // Período de gracia parcial: solo interés
                interestDueGrace = remainingBalance.multiply(monthlyRate)
                        .setScale(2, RoundingMode.HALF_UP);
                installmentDueGrace = interestDueGrace;
            } else if (gracePeriodType.isTotal()) {
                // Período de gracia total: sin interés ni capital
                installmentDueGrace = BigDecimal.ZERO;
            }

            PaymentScheduleItem item = PaymentScheduleItem.builder()
                    .creditId(loan.getId())  // ✅ CORRECTO: loan.getId() retorna Long
                    .userId(loan.getUserId())
                    .period(period)
                    .installment(installmentDueGrace)
                    .interest(interestDueGrace)
                    .amortization(amortizationDueGrace)
                    .remainingBalance(remainingBalance)
                    .isGracePeriod(true)
                    .isPaid(false)
                    .dueDate(dueDate)
                    .build();

            schedule.add(item);
        }

        // Generar períodos de amortización regular
        for (int period = gracePeriods + 1; period <= totalPeriods; period++) {
            calendar.add(Calendar.MONTH, 1);
            Date dueDate = (Date) calendar.getTime().clone();

            // Interés = Saldo pendiente * Tasa mensual
            BigDecimal interest = remainingBalance.multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            // Amortización = Cuota fija - Interés
            BigDecimal amortization = fixedInstallment.subtract(interest)
                    .setScale(2, RoundingMode.HALF_UP);

            // Saldo restante = Saldo anterior - Amortización
            remainingBalance = remainingBalance.subtract(amortization)
                    .setScale(2, RoundingMode.HALF_UP);

            // En la última cuota, ajustar por redondeo
            if (period == totalPeriods && remainingBalance.compareTo(BigDecimal.ZERO) != 0) {
                amortization = amortization.add(remainingBalance);
                remainingBalance = BigDecimal.ZERO;
            }

            PaymentScheduleItem item = PaymentScheduleItem.builder()
                    .creditId(loan.getId())
                    .userId(loan.getUserId())
                    .period(period)
                    .installment(fixedInstallment)
                    .interest(interest)
                    .amortization(amortization)
                    .remainingBalance(remainingBalance)
                    .isGracePeriod(false)
                    .isPaid(false)
                    .dueDate(dueDate)
                    .build();

            schedule.add(item);
        }

        return schedule;
    }
}