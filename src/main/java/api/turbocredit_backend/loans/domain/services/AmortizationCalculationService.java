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
        BigDecimal remainingBalance = loan.getLoanAmount(); // Saldo inicial: Monto del préstamo (Vehículo - Cuota Inicial)
        BigDecimal fixedInstallment = loan.getFixedInstallment();

        int gracePeriods = loan.getGracePeriodMonths() != null ? loan.getGracePeriodMonths() : 0;
        int totalPeriods = loan.getTermMonths();
        GracePeriodType gracePeriodType = loan.getGracePeriodType();

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        // Generar períodos de gracia
        for (int period = 1; period <= gracePeriods; period++) {
            calendar.add(Calendar.MONTH, 1);
            Date dueDate = (Date) calendar.getTime().clone();

            BigDecimal interestDueGrace = remainingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizationDueGrace = BigDecimal.ZERO;
            BigDecimal installmentDueGrace = BigDecimal.ZERO;

            if (gracePeriodType.isPartial()) {
                // Gracia parcial: se paga interés, el saldo no cambia
                installmentDueGrace = interestDueGrace;
            } else {
                // Gracia total: no se paga interés ni capital, el interés capitaliza
                remainingBalance = remainingBalance.add(interestDueGrace);
            }

            // Seguros y gastos mensuales
            BigDecimal lifeInsurance = remainingBalance.multiply(loan.getSeguroDesgravamenRate()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal vehicularInsurance = loan.getSeguroVehicularMonthly() != null ? loan.getSeguroVehicularMonthly() : BigDecimal.ZERO;
            BigDecimal portes = loan.getPortes() != null ? loan.getPortes() : BigDecimal.ZERO;

            BigDecimal totalInstallment = installmentDueGrace
                    .add(lifeInsurance)
                    .add(vehicularInsurance)
                    .add(portes)
                    .setScale(2, RoundingMode.HALF_UP);

            PaymentScheduleItem item = PaymentScheduleItem.builder()
                    .creditId(loan.getId())
                    .userId(loan.getUserId())
                    .period(period)
                    .installment(installmentDueGrace)
                    .interest(interestDueGrace)
                    .amortization(amortizationDueGrace)
                    .lifeInsurance(lifeInsurance)
                    .vehicularInsurance(vehicularInsurance)
                    .portes(portes)
                    .totalInstallment(totalInstallment)
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

            // Saldo inicial del periodo para el seguro de desgravamen
            BigDecimal balanceBeforePayment = remainingBalance;

            // Interés = Saldo pendiente * Tasa mensual
            BigDecimal interest = balanceBeforePayment.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

            BigDecimal amortization;
            BigDecimal installment;

            // Si es el último periodo (mes N), se paga la cuota fija base más el valor residual (VF)
            if (period == totalPeriods) {
                // La amortización es exactamente el saldo restante
                amortization = remainingBalance;
                installment = amortization.add(interest);
                remainingBalance = BigDecimal.ZERO;
            } else {
                // Amortización = Cuota fija - Interés
                installment = fixedInstallment;
                amortization = installment.subtract(interest).setScale(2, RoundingMode.HALF_UP);
                remainingBalance = remainingBalance.subtract(amortization).setScale(2, RoundingMode.HALF_UP);
            }

            // Seguros y gastos mensuales
            BigDecimal lifeInsurance = balanceBeforePayment.multiply(loan.getSeguroDesgravamenRate()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal vehicularInsurance = loan.getSeguroVehicularMonthly() != null ? loan.getSeguroVehicularMonthly() : BigDecimal.ZERO;
            BigDecimal portes = loan.getPortes() != null ? loan.getPortes() : BigDecimal.ZERO;

            BigDecimal totalInstallment = installment
                    .add(lifeInsurance)
                    .add(vehicularInsurance)
                    .add(portes)
                    .setScale(2, RoundingMode.HALF_UP);

            PaymentScheduleItem item = PaymentScheduleItem.builder()
                    .creditId(loan.getId())
                    .userId(loan.getUserId())
                    .period(period)
                    .installment(installment)
                    .interest(interest)
                    .amortization(amortization)
                    .lifeInsurance(lifeInsurance)
                    .vehicularInsurance(vehicularInsurance)
                    .portes(portes)
                    .totalInstallment(totalInstallment)
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