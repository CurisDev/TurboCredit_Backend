package api.turbocredit_backend.loans.domain.model.aggregates;

import api.turbocredit_backend.loans.domain.exceptions.InvalidInterestRateException;
import api.turbocredit_backend.loans.domain.exceptions.InvalidLoanAmountException;
import api.turbocredit_backend.loans.domain.model.valueobjects.*;
import api.turbocredit_backend.shared.domain.model.aggregates.AuditableAbstractAggregateRootWithLongId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(name = "vehicle_credits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCredit extends AuditableAbstractAggregateRootWithLongId<VehicleCredit> {  // ✅ CAMBIO: Nueva clase base

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "vehicle_price", nullable = false)
    private BigDecimal vehiclePrice;

    @NotNull
    @Column(name = "down_payment", nullable = false)
    private BigDecimal downPayment;

    @NotNull
    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "interest_rate")),
            @AttributeOverride(name = "type", column = @Column(name = "rate_type"))
    })
    private InterestRate interestRate;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "grace_period_months", nullable = false)
    private Integer gracePeriodMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "grace_period_type", nullable = false)
    private GracePeriodType gracePeriodType;

    @Embedded
    @AttributeOverride(name = "code", column = @Column(name = "currency"))
    private Currency currency;

    @Column(name = "insurance_cost", nullable = false)
    private BigDecimal insuranceCost = BigDecimal.ZERO;

    // Atributos calculados
    @Column(name = "principal_financed", nullable = false)
    private BigDecimal principalFinanced;

    @Column(name = "periodic_rate", nullable = false)
    private BigDecimal periodicRate;

    @Column(name = "fixed_installment", nullable = false)
    private BigDecimal fixedInstallment;

    @Column(name = "total_interest_paid", nullable = false)
    private BigDecimal totalInterestPaid = BigDecimal.ZERO;

    @Column(name = "total_paid", nullable = false)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "npv", precision = 15, scale = 2)
    private BigDecimal npv; // Valor Presente Neto

    @Column(name = "irr", precision = 10, scale = 6)
    private BigDecimal irr; // Tasa Interna de Retorno

    @Column(name = "tcea", precision = 10, scale = 6)
    private BigDecimal tcea; // Tasa de Costo Efectivo Anual

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status = LoanStatus.PENDING;

    @Column(name = "bank_name")
    private String bankName;

    public VehicleCredit(UUID userId, BigDecimal vehiclePrice, BigDecimal downPayment,
                         BigDecimal loanAmount, InterestRate interestRate, Integer termMonths,
                         Integer gracePeriodMonths, GracePeriodType gracePeriodType,
                         Currency currency, BigDecimal insuranceCost) {
        if (loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidLoanAmountException(loanAmount);
        }
        if (interestRate == null) {
            throw new InvalidInterestRateException("Interest rate cannot be null");
        }

        this.userId = userId;
        this.vehiclePrice = vehiclePrice;
        this.downPayment = downPayment;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.gracePeriodMonths = gracePeriodMonths;
        this.gracePeriodType = gracePeriodType;
        this.currency = currency;
        this.insuranceCost = insuranceCost;

        // Calcula principal financiado
        this.principalFinanced = loanAmount.add(insuranceCost);

        // Calcula tasa periódica
        this.periodicRate = interestRate.toMonthlyEffectiveRate();
    }

    public void approve() {
        this.status = LoanStatus.APPROVED;
    }

    public void activate() {
        this.status = LoanStatus.ACTIVE;
    }

    public void complete() {
        this.status = LoanStatus.COMPLETED;
    }

    /**
     * Calcula la cuota fija usando el método francés
     * Fórmula: C = P * [r(1+r)^n] / [(1+r)^n - 1]
     */
    public BigDecimal calculateFixedInstallment() {
        BigDecimal r = periodicRate.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
        BigDecimal n = new BigDecimal(termMonths - gracePeriodMonths);

        BigDecimal onePlusRPowN = BigDecimal.ONE.add(r).pow(n.intValue());
        BigDecimal numerator = r.multiply(onePlusRPowN);
        BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Invalid calculation: denominator is zero");
        }

        this.fixedInstallment = principalFinanced
                .multiply(numerator)
                .divide(denominator, 2, RoundingMode.HALF_UP);

        return this.fixedInstallment;
    }

    /**
     * Calcula el interés total pagado
     */
    public BigDecimal calculateTotalInterest() {
        BigDecimal totalPayments = fixedInstallment.multiply(new BigDecimal(termMonths - gracePeriodMonths));
        this.totalInterestPaid = totalPayments.subtract(principalFinanced);
        return this.totalInterestPaid;
    }

    /**
     * Calcula el total a pagar
     */
    public BigDecimal calculateTotalPaid() {
        this.totalPaid = principalFinanced.add(totalInterestPaid);
        return this.totalPaid;
    }
}