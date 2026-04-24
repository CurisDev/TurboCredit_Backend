package api.turbocredit_backend.loans.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreditResource {

    @Schema(description = "Credit ID", example = "1")
    private Long id;

    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Vehicle price", example = "50000.00")
    private BigDecimal vehiclePrice;

    @Schema(description = "Down payment amount", example = "10000.00")
    private BigDecimal downPayment;

    @Schema(description = "Loan amount to finance", example = "40000.00")
    private BigDecimal loanAmount;

    @Schema(description = "Annual interest rate", example = "8.5")
    private BigDecimal interestRate;

    @Schema(description = "Interest rate type", example = "EFFECTIVE")
    private String rateType;

    @Schema(description = "Term in months", example = "60")
    private Integer termMonths;

    @Schema(description = "Grace period in months", example = "0")
    private Integer gracePeriodMonths;

    @Schema(description = "Grace period type", example = "PARTIAL")
    private String gracePeriodType;

    @Schema(description = "Currency code", example = "PEN")
    private String currency;

    @Schema(description = "Insurance cost", example = "500.00")
    private BigDecimal insuranceCost;

    @Schema(description = "Principal financed (includes insurance)", example = "40500.00")
    private BigDecimal principalFinanced;

    @Schema(description = "Monthly interest rate percentage", example = "0.68")
    private BigDecimal periodicRate;

    @Schema(description = "Fixed monthly installment", example = "836.50")
    private BigDecimal fixedInstallment;

    @Schema(description = "Total interest paid over loan term", example = "10190.00")
    private BigDecimal totalInterestPaid;

    @Schema(description = "Total amount paid", example = "50690.00")
    private BigDecimal totalPaid;

    @Schema(description = "Net Present Value (VAN)", example = "0.00")
    private BigDecimal npv;

    @Schema(description = "Internal Rate of Return (TIR)", example = "8.50")
    @JsonProperty("irr")
    private BigDecimal irr;

    @Schema(description = "Annual Effective Cost Rate (TCEA)", example = "8.75")
    private BigDecimal tcea;

    @Schema(description = "Loan status", example = "PENDING")
    private String status;

    @Schema(description = "Bank name", example = "BCP")
    private String bankName;
}