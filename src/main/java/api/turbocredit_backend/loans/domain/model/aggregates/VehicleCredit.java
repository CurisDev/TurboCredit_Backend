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

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_dni")
    private String clientDni;

    @Column(name = "vehicle_brand")
    private String vehicleBrand;

    @Column(name = "vehicle_model")
    private String vehicleModel;

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

    // Campos de Compra Inteligente y tasas/seguros de bancos
    @Column(name = "residual_percentage", precision = 5, scale = 2)
    private BigDecimal residualPercentage = BigDecimal.ZERO;

    @Column(name = "residual_value", precision = 15, scale = 2)
    private BigDecimal residualValue = BigDecimal.ZERO;

    @Column(name = "seguro_desgravamen_rate", precision = 10, scale = 6)
    private BigDecimal seguroDesgravamenRate = BigDecimal.ZERO;

    @Column(name = "seguro_vehicular_monthly", precision = 15, scale = 2)
    private BigDecimal seguroVehicularMonthly = BigDecimal.ZERO;

    @Column(name = "portes", precision = 15, scale = 2)
    private BigDecimal portes = BigDecimal.ZERO;


    @Column(name = "cok", precision = 10, scale = 6)
    private BigDecimal cok = BigDecimal.ZERO;

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

    public void setNpv(BigDecimal npv) {
        this.npv = npv;
    }

    public void setIrr(BigDecimal irr) {
        this.irr = irr;
    }

    public void setTcea(BigDecimal tcea) {
        this.tcea = tcea;
    }

    public BigDecimal getIrr() {
        return this.irr;
    }

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

        // Inicializa principal y tasa periódica
        this.principalFinanced = loanAmount;
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
     * Calcula la cuota fija usando el método francés de Compra Inteligente (con cuota residual final)
     */
    public BigDecimal calculateFixedInstallment() {
        double r = periodicRate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).doubleValue();
        int g = gracePeriodMonths != null ? gracePeriodMonths : 0;
        int total = termMonths != null ? termMonths : 1;
        int n = total - g;

        if (n <= 0) {
            throw new ArithmeticException("Term months must be greater than grace periods");
        }

        // Calcula capitalización durante el período de gracia total: L_g = L_0 * (1 + r)^g
        double loanAmountDouble = loanAmount.doubleValue();
        double l_g = loanAmountDouble * Math.pow(1 + r, g);

        // Calcula el valor residual final (VF)
        double vf = 0;
        if (residualPercentage != null && residualPercentage.compareTo(BigDecimal.ZERO) > 0) {
            this.residualValue = vehiclePrice.multiply(residualPercentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            vf = this.residualValue.doubleValue();
        } else if (residualValue != null) {
            vf = residualValue.doubleValue();
        }

        // Principal ajustado: L_adj = L_g - VF / (1 + r)^n
        double l_adj = l_g - (vf / Math.pow(1 + r, n));

        double installmentDouble;
        if (r == 0) {
            installmentDouble = l_adj / n;
        } else {
            // Fórmula cuota francesa: C = L_adj * [r * (1 + r)^n] / [(1 + r)^n - 1]
            double factor = Math.pow(1 + r, n);
            installmentDouble = l_adj * (r * factor) / (factor - 1);
        }

        this.fixedInstallment = BigDecimal.valueOf(installmentDouble).setScale(2, RoundingMode.HALF_UP);
        this.principalFinanced = BigDecimal.valueOf(l_g).setScale(2, RoundingMode.HALF_UP);

        return this.fixedInstallment;
    }

    /**
     * Calcula el interés total pagado
     */
    public BigDecimal calculateTotalInterest() {
        int g = gracePeriodMonths != null ? gracePeriodMonths : 0;
        int total = termMonths != null ? termMonths : 1;
        int n = total - g;
        BigDecimal payments = fixedInstallment.multiply(BigDecimal.valueOf(n));
        if (residualValue != null) {
            payments = payments.add(residualValue);
        }
        this.totalInterestPaid = payments.subtract(loanAmount);
        return this.totalInterestPaid;
    }

    /**
     * Calcula el total a pagar
     */
    public BigDecimal calculateTotalPaid() {
        int g = gracePeriodMonths != null ? gracePeriodMonths : 0;
        int total = termMonths != null ? termMonths : 1;
        int n = total - g;
        BigDecimal payments = fixedInstallment.multiply(BigDecimal.valueOf(n));
        if (residualValue != null) {
            payments = payments.add(residualValue);
        }
        this.totalPaid = payments;
        return this.totalPaid;
    }
}